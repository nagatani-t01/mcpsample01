package org.springframework.samples.petclinic.mcp.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 既存 API の Pet JSON（種別・飼い主 ID・来院記録を含む）。 */
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