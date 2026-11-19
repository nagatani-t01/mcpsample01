package org.springframework.samples.petclinic.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.samples.petclinic.mcp.config.McpServerProperties;
import org.springframework.samples.petclinic.mcp.config.PetclinicApiProperties;

@SpringBootApplication
@EnableConfigurationProperties({PetclinicApiProperties.class, McpServerProperties.class})
public class PetclinicMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetclinicMcpServerApplication.class, args);
    }
}