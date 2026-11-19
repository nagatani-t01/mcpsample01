package org.springframework.samples.petclinic.mcp.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** ペット種別（id と name）。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PetType(Integer id, String name) {
}