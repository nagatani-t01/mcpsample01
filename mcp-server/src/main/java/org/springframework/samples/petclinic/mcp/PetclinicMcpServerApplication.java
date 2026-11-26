package org.springframework.samples.petclinic.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.samples.petclinic.mcp.config.McpServerProperties;
import org.springframework.samples.petclinic.mcp.config.McpTraceProperties;
import org.springframework.samples.petclinic.mcp.config.PetclinicApiProperties;
import org.springframework.samples.petclinic.mcp.privacy.PersonalDataConsentProperties;

/**
 * MCP サーバーの起動エントリポイント。
 * Spring AI MCP（Streamable HTTP）を有効にし、{@code @McpTool} / {@code @McpResource} 付き Bean を自動登録する。
 */
@SpringBootApplication
@EnableConfigurationProperties({
    PetclinicApiProperties.class,
    McpServerProperties.class,
    McpTraceProperties.class,
    PersonalDataConsentProperties.class
})
public class PetclinicMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetclinicMcpServerApplication.class, args);
    }
}