package org.springframework.samples.petclinic.mcp.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** ペット登録用の編集可能フィールド。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PetFields(String name, String birthDate, PetType type) {
}