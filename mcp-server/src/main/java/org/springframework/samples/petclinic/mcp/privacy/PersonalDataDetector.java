package org.springframework.samples.petclinic.mcp.privacy;

import java.lang.reflect.RecordComponent;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.samples.petclinic.mcp.client.dto.Owner;
import org.springframework.samples.petclinic.mcp.client.dto.OwnerFields;
import org.springframework.samples.petclinic.mcp.client.dto.Pet;
import org.springframework.samples.petclinic.mcp.client.dto.Vet;
import org.springframework.samples.petclinic.mcp.client.dto.Visit;
import org.springframework.stereotype.Component;

/**
 * API / ツール戻り値が個人情報を含むかを判定する。
 * 飼い主・獣医の氏名住所電話、顧客ペット、来院記録を対象とする。
 */
@Component
public class PersonalDataDetector {

    private static final Set<String> PII_RECORD_FIELD_NAMES = Set.of(
        "firstname",
        "lastname",
        "address",
        "telephone",
        "phone",
        "email",
        "city"
    );

    public boolean containsPersonalData(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Owner || value instanceof OwnerFields || value instanceof Vet
            || value instanceof Pet || value instanceof Visit) {
            return true;
        }
        if (value instanceof Collection<?> collection) {
            for (Object element : collection) {
                if (containsPersonalData(element)) {
                    return true;
                }
            }
            return false;
        }
        if (value instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null && isPiiFieldName(String.valueOf(entry.getKey()))) {
                    return true;
                }
                if (containsPersonalData(entry.getValue())) {
                    return true;
                }
            }
            return false;
        }
        if (value instanceof Object[] array) {
            for (Object element : array) {
                if (containsPersonalData(element)) {
                    return true;
                }
            }
            return false;
        }
        Class<?> type = value.getClass();
        if (type.isRecord()) {
            for (RecordComponent component : type.getRecordComponents()) {
                if (isPiiFieldName(component.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isPiiFieldName(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }
        String normalized = name.replace("_", "").replace("-", "").toLowerCase();
        return PII_RECORD_FIELD_NAMES.contains(normalized);
    }
}
