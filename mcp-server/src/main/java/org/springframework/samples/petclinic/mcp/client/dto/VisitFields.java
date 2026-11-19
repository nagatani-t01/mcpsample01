package org.springframework.samples.petclinic.mcp.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 来院記録登録用の編集可能フィールド。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VisitFields(String date, String description) {
}