package org.springframework.samples.petclinic.mcp.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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