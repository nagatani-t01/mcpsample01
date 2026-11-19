package org.springframework.samples.petclinic.mcp.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OwnerFields(
    String firstName,
    String lastName,
    String address,
    String city,
    String telephone
) {
}