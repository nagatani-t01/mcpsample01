package org.springframework.samples.petclinic.mcp.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "petclinic.api")
public class PetclinicApiProperties {

    private String baseUrl = "http://localhost:9966/petclinic/api";

    private Duration timeout = Duration.ofSeconds(10);

    private String username = "";

    private String password = "";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

  /** OpenAPI YAML served by springdoc on the backend (outside {@code /api}). */
    public String openApiDocsUrl() {
        String root = baseUrl.endsWith("/api") ? baseUrl.substring(0, baseUrl.length() - 4) : baseUrl;
        return root + "/v3/api-docs.yaml";
    }
}