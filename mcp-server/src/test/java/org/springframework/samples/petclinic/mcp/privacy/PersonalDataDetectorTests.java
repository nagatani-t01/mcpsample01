package org.springframework.samples.petclinic.mcp.privacy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.mcp.client.dto.Owner;
import org.springframework.samples.petclinic.mcp.client.dto.Pet;
import org.springframework.samples.petclinic.mcp.client.dto.PetType;
import org.springframework.samples.petclinic.mcp.client.dto.Vet;

class PersonalDataDetectorTests {

    private final PersonalDataDetector detector = new PersonalDataDetector();

    @Test
    void detectsOwner() {
        Owner owner = new Owner(1, "A", "B", "addr", "city", "123", List.of());
        assertThat(detector.containsPersonalData(owner)).isTrue();
        assertThat(detector.containsPersonalData(List.of(owner))).isTrue();
    }

    @Test
    void detectsPetAndVet() {
        assertThat(detector.containsPersonalData(
            new Pet(1, 1, "Leo", "2010-01-01", new PetType(1, "cat"), List.of()))).isTrue();
        assertThat(detector.containsPersonalData(new Vet(1, "James", "Carter", List.of()))).isTrue();
    }

    @Test
    void ignoresPetTypeAndNull() {
        assertThat(detector.containsPersonalData(new PetType(1, "cat"))).isFalse();
        assertThat(detector.containsPersonalData(null)).isFalse();
        assertThat(detector.containsPersonalData("ok")).isFalse();
    }

    @Test
    void detectsPiiKeysInMap() {
        assertThat(detector.containsPersonalData(Map.of("telephone", "6085551023"))).isTrue();
        assertThat(detector.containsPersonalData(Map.of("status", "granted"))).isFalse();
    }
}
