package org.springframework.samples.petclinic.mcp.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 飼い主登録用の編集可能フィールド（OpenAPI の OwnerFields に相当）。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OwnerFields(
    String firstName,
    String lastName,
    String address,
    String city,
    String telephone
) {
}