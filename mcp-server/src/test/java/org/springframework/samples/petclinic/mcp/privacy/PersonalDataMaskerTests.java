package org.springframework.samples.petclinic.mcp.privacy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.mcp.client.dto.Owner;
import org.springframework.samples.petclinic.mcp.client.dto.Pet;
import org.springframework.samples.petclinic.mcp.client.dto.PetType;
import org.springframework.samples.petclinic.mcp.client.dto.Vet;
import org.springframework.samples.petclinic.mcp.client.dto.Visit;

class PersonalDataMaskerTests {

    private final PseudonymDictionary dictionary = new PseudonymDictionary();

    private final PersonalDataMasker masker = new PersonalDataMasker(dictionary);

    @BeforeEach
    void setSession() {
        McpSessionContext.set("masker-test");
        dictionary.clearCurrentSession();
    }

    @AfterEach
    void clear() {
        McpSessionContext.clear();
    }

    @Test
    void keepsIdsAndStablePseudonymsWithinSession() {
        Owner first = (Owner) masker.mask(
            new Owner(1, "George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023", List.of())
        );
        Owner second = (Owner) masker.mask(
            new Owner(1, "George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023", List.of())
        );

        assertThat(first.id()).isEqualTo(1);
        assertThat(first.firstName()).isEqualTo(second.firstName());
        assertThat(first.lastName()).isEqualTo(second.lastName());
        assertThat(first.firstName()).isNotEqualTo("George");
        assertThat(first.lastName()).isNotEqualTo("Franklin");
        assertThat(first.address()).isEqualTo(PersonalDataMasker.redactAddress("110 W. Liberty St."));
        assertThat(first.telephone()).isEqualTo(PersonalDataMasker.redactTelephone("6085551023"));
        assertThat(second.address()).isEqualTo(first.address());
        assertThat(second.telephone()).isEqualTo(first.telephone());
    }

    @Test
    void partiallyRedactsAddressAndTelephone() {
        Owner masked = (Owner) masker.mask(
            new Owner(1, "太郎", "山田", "千代田区丸の内1-1-1", "東京", "03011112222", List.of())
        );

        assertThat(masked.address()).isEqualTo("千代田＊＊＊＊＊＊＊＊＊");
        assertThat(masked.telephone()).isEqualTo("030＊＊＊＊＊＊22");
        assertThat(masked.address()).startsWith("千代田");
        assertThat(masked.address()).doesNotContain("丸の内");
        assertThat(masked.telephone()).startsWith("030");
        assertThat(masked.telephone()).endsWith("22");
        assertThat(masked.telephone()).doesNotContain("1111");
    }

    @Test
    void masksNestedPetsAndVisits() {
        Visit visit = new Visit(9, 2, "2024-01-01", "rabies shot");
        PetType dog = new PetType(2, "犬");
        Pet pet = new Pet(2, 1, "Leo", "2010-09-07", dog, List.of(visit));
        Owner owner = new Owner(1, "George", "Franklin", "addr", "city", "123", List.of(pet));

        Owner masked = (Owner) masker.mask(owner);

        assertThat(masked.pets()).hasSize(1);
        Pet maskedPet = masked.pets().get(0);
        assertThat(maskedPet.id()).isEqualTo(2);
        assertThat(maskedPet.name()).isNotEqualTo("Leo");
        assertThat(maskedPet.type()).isSameAs(dog);
        assertThat(maskedPet.type().name()).isEqualTo("犬");
        assertThat(maskedPet.visits().get(0).id()).isEqualTo(9);
        assertThat(maskedPet.visits().get(0).description()).isNotEqualTo("rabies shot");
    }

    @Test
    void keepsPetTypeUnmasked() {
        PetType type = new PetType(2, "犬");
        Pet masked = (Pet) masker.mask(new Pet(3, 3, "Rosy", "2011-04-17", type, List.of()));

        assertThat(masked.name()).isNotEqualTo("Rosy");
        assertThat(masked.type().id()).isEqualTo(2);
        assertThat(masked.type().name()).isEqualTo("犬");
    }

    @Test
    void masksVetNamesButKeepsSpecialties() {
        Vet vet = new Vet(1, "James", "Carter", List.of());
        Vet masked = (Vet) masker.mask(vet);

        assertThat(masked.id()).isEqualTo(1);
        assertThat(masked.firstName()).isNotEqualTo("James");
        assertThat(masked.lastName()).isNotEqualTo("Carter");
    }
}
