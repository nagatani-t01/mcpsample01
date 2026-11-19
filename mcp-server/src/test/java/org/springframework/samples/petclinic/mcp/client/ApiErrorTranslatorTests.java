package org.springframework.samples.petclinic.mcp.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.mcp.config.PetclinicApiProperties;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

class ApiErrorTranslatorTests {

    private ApiErrorTranslator translator;

    @BeforeEach
    void setUp() {
        PetclinicApiProperties properties = new PetclinicApiProperties();
        properties.setBaseUrl("http://localhost:9966/petclinic/api");
        translator = new ApiErrorTranslator(new ObjectMapper(), properties);
    }

    @Test
    void notFoundUsesHint() {
        RestClientResponseException ex = response(HttpStatus.NOT_FOUND, "", new HttpHeaders());
        PetclinicApiException result = translator.translateResponse(ex, "飼い主 ID 999 は見つかりません。");
        assertThat(result.getMessage()).contains("999");
    }

    @Test
    void validationErrorsFromHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("errors", "[{\"fieldName\":\"telephone\",\"fieldValue\":\"abc\",\"errorMessage\":\"must match\"}]");
        RestClientResponseException ex = response(HttpStatus.BAD_REQUEST, "", headers);
        PetclinicApiException result = translator.translateResponse(ex, null);
        assertThat(result.getMessage()).contains("telephone");
        assertThat(result.getMessage()).doesNotContain("must match \"");
    }

    @Test
    void genericBadRequestHidesSql() {
        String body = "{\"className\":\"org.springframework.dao.DataIntegrityViolationException\","
            + "\"exMessage\":\"insert into visits ...\"}";
        RestClientResponseException ex = response(HttpStatus.BAD_REQUEST, body, new HttpHeaders());
        PetclinicApiException result = translator.translateResponse(ex, null);
        assertThat(result.getMessage()).contains("DataIntegrityViolationException");
        assertThat(result.getMessage()).doesNotContain("insert into");
    }

    @Test
    void connectionFailureMessage() {
        PetclinicApiException result = translator.translateConnection(new ResourceAccessException("refused"));
        assertThat(result.getMessage()).contains("接続できません");
    }

    private static RestClientResponseException response(HttpStatus status, String body, HttpHeaders headers) {
        return new RestClientResponseException("error", status.value(), status.getReasonPhrase(), headers, body.getBytes(), null);
    }
}