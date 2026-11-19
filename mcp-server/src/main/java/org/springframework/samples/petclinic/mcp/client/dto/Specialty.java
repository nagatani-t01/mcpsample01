package org.springframework.samples.petclinic.mcp.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Specialty(Integer id, String name) {
}