package org.springframework.samples.petclinic.mcp.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 獣医の専門分野。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Specialty(Integer id, String name) {
}