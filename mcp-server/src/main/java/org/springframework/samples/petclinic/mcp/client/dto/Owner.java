package org.springframework.samples.petclinic.mcp.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 既存 API の Owner JSON をマッピング（ペット一覧を含む）。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Owner(
    Integer id,
    String firstName,
    String lastName,
    String address,
    String city,
    String telephone,
    List<Pet> pets
) {
}