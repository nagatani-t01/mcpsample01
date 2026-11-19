package org.springframework.samples.petclinic.mcp.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 来院記録（日付・内容・ペット ID）。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Visit(Integer id, Integer petId, String date, String description) {
}