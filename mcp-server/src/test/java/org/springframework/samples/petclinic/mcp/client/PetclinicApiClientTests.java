package org.springframework.samples.petclinic.mcp.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.mcp.config.PetclinicApiProperties;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PetclinicApiClientTests {

    private MockRestServiceServer server;

    private PetclinicApiClient client;

    @BeforeEach
    void setUp() {
        PetclinicApiProperties properties = new PetclinicApiProperties();
        properties.setBaseUrl("http://localhost:9966/petclinic/api");
        RestClient.Builder builder = RestClient.builder().baseUrl(properties.getBaseUrl());
        server = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();
        ApiErrorTranslator translator = new ApiErrorTranslator(new ObjectMapper(), properties);
        client = new PetclinicApiClient(restClient, restClient, translator, properties);
    }

    @AfterEach
    void verify() {
        server.verify();
    }

    @Test
    void listOwnersWithLastName() {
        server.expect(requestTo("http://localhost:9966/petclinic/api/owners?lastName=Davis"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess("[{\"id\":1,\"lastName\":\"Davis\"}]", MediaType.APPLICATION_JSON));
        assertThat(client.listOwners("Davis")).hasSize(1);
    }

    @Test
    void listOwnersWithoutFilter() {
        server.expect(requestTo("http://localhost:9966/petclinic/api/owners"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        assertThat(client.listOwners(null)).isEmpty();
    }
}