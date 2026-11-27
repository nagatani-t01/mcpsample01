package org.springframework.samples.petclinic.mcp.privacy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.samples.petclinic.mcp.client.dto.Owner;
import org.springframework.samples.petclinic.mcp.client.dto.OwnerFields;
import org.springframework.samples.petclinic.mcp.client.dto.Pet;
import org.springframework.samples.petclinic.mcp.client.dto.Vet;
import org.springframework.samples.petclinic.mcp.client.dto.Visit;
import org.springframework.stereotype.Component;

/**
 * 個人情報を含むオブジェクトを保護して返す（mcp-server-conceal の擬似匿名化を模倣）。
 * ID・ペット種別（犬・猫など）・診療科はそのまま残し、
 * 氏名・市区町村・ペット名などは仮名化、住所・電話は一部を伏字（＊）にする。
 */
@Component
public class PersonalDataMasker {

    private static final int ADDRESS_VISIBLE_PREFIX = 3;

    private static final int TELEPHONE_VISIBLE_PREFIX = 3;

    private static final int TELEPHONE_VISIBLE_SUFFIX = 2;

    private final PseudonymDictionary dictionary;

    public PersonalDataMasker(PseudonymDictionary dictionary) {
        this.dictionary = dictionary;
    }

    /** 住所: 先頭数文字を残し、以降を ＊ にする（例: 千代田＊＊＊＊＊＊＊＊＊＊）。 */
    static String redactAddress(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        int[] codePoints = value.codePoints().toArray();
        int keep = Math.min(ADDRESS_VISIBLE_PREFIX, Math.max(1, codePoints.length - 1));
        return prefix(codePoints, keep) + "＊".repeat(codePoints.length - keep);
    }

    /** 電話: 先頭と末尾を残し、中間を ＊ にする（例: 030＊＊＊＊＊＊22）。 */
    static String redactTelephone(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        int[] codePoints = value.codePoints().toArray();
        if (codePoints.length <= TELEPHONE_VISIBLE_PREFIX + TELEPHONE_VISIBLE_SUFFIX) {
            int keep = Math.max(1, codePoints.length - 1);
            return prefix(codePoints, keep) + "＊".repeat(codePoints.length - keep);
        }
        int middle = codePoints.length - TELEPHONE_VISIBLE_PREFIX - TELEPHONE_VISIBLE_SUFFIX;
        return prefix(codePoints, TELEPHONE_VISIBLE_PREFIX)
            + "＊".repeat(middle)
            + suffix(codePoints, TELEPHONE_VISIBLE_SUFFIX);
    }

    private static String prefix(int[] codePoints, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.appendCodePoint(codePoints[i]);
        }
        return sb.toString();
    }

    private static String suffix(int[] codePoints, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = codePoints.length - count; i < codePoints.length; i++) {
            sb.appendCodePoint(codePoints[i]);
        }
        return sb.toString();
    }

    public Object mask(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Owner owner) {
            return maskOwner(owner);
        }
        if (value instanceof OwnerFields fields) {
            return maskOwnerFields(fields);
        }
        if (value instanceof Vet vet) {
            return maskVet(vet);
        }
        if (value instanceof Pet pet) {
            return maskPet(pet);
        }
        if (value instanceof Visit visit) {
            return maskVisit(visit);
        }
        if (value instanceof List<?> list) {
            List<Object> masked = new ArrayList<>(list.size());
            for (Object element : list) {
                masked.add(mask(element));
            }
            return masked;
        }
        if (value instanceof Collection<?> collection) {
            List<Object> masked = new ArrayList<>(collection.size());
            for (Object element : collection) {
                masked.add(mask(element));
            }
            return masked;
        }
        if (value instanceof Map<?, ?> map) {
            Map<Object, Object> masked = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                masked.put(entry.getKey(), mask(entry.getValue()));
            }
            return masked;
        }
        return value;
    }

    /**
     * ツール結果を、仮名化済みであることの注釈付きで返す。
     */
    public Map<String, Object> wrapMaskedResult(Object maskedData) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("masked", true);
        body.put(
            "message",
            "個人情報は仮名化されています（実データではありません）。"
                + "実名の取得・表示にはユーザー同意のうえで grant_personal_data_consent を実行し、同じツールを再実行してください。"
        );
        body.put("data", maskedData);
        return body;
    }

    private Owner maskOwner(Owner owner) {
        List<Pet> pets = owner.pets() == null ? null : owner.pets().stream().map(this::maskPet).toList();
        return new Owner(
            owner.id(),
            dictionary.map("firstName", owner.firstName()),
            dictionary.map("lastName", owner.lastName()),
            redactAddress(owner.address()),
            dictionary.map("city", owner.city()),
            redactTelephone(owner.telephone()),
            pets
        );
    }

    private OwnerFields maskOwnerFields(OwnerFields fields) {
        return new OwnerFields(
            dictionary.map("firstName", fields.firstName()),
            dictionary.map("lastName", fields.lastName()),
            redactAddress(fields.address()),
            dictionary.map("city", fields.city()),
            redactTelephone(fields.telephone())
        );
    }

    private Vet maskVet(Vet vet) {
        return new Vet(
            vet.id(),
            dictionary.map("firstName", vet.firstName()),
            dictionary.map("lastName", vet.lastName()),
            vet.specialties()
        );
    }

    private Pet maskPet(Pet pet) {
        List<Visit> visits = pet.visits() == null ? null : pet.visits().stream().map(this::maskVisit).toList();
        // ペット名だけ仮名化。種別（type）は仮名化しない
        return new Pet(
            pet.id(),
            pet.ownerId(),
            dictionary.map("petName", pet.name()),
            pet.birthDate(),
            pet.type(),
            visits
        );
    }

    private Visit maskVisit(Visit visit) {
        // 診療メモに氏名が混ざる可能性があるため内容も仮名化対象にする
        return new Visit(
            visit.id(),
            visit.petId(),
            visit.date(),
            dictionary.map("visitDescription", visit.description())
        );
    }
}
