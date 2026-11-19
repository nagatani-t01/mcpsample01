package org.springframework.samples.petclinic.mcp.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Vet(Integer id, String firstName, String lastName, List<Specialty> specialties) {
}