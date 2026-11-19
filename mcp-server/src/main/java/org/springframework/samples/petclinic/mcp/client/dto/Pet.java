package org.springframework.samples.petclinic.mcp.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Pet(
    Integer id,
    Integer ownerId,
    String name,
    String birthDate,
    PetType type,
    List<Visit> visits
) {
}