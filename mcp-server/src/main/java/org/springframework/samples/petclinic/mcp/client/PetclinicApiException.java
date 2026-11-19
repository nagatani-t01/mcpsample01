package org.springframework.samples.petclinic.mcp.client;

/** User- and model-facing API error; becomes an MCP error tool result when thrown from tools. */
public class PetclinicApiException extends RuntimeException {

    public PetclinicApiException(String message) {
        super(message);
    }
}