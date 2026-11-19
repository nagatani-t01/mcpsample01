package org.springframework.samples.petclinic.mcp.resources;

import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.samples.petclinic.mcp.client.PetclinicApiClient;
import org.springframework.stereotype.Component;

@Component
public class OpenApiResource {

    private final PetclinicApiClient apiClient;

    public OpenApiResource(PetclinicApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @McpResource(
        uri = "petclinic://openapi.yml",
        name = "Petclinic API 仕様",
        description = "バックエンドの OpenAPI 仕様（springdoc が生成）",
        mimeType = "application/yaml"
    )
    public String openApiSpec() {
        return apiClient.fetchOpenApiSpec();
    }
}