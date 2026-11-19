package org.springframework.samples.petclinic.mcp.tools;

import java.util.List;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.samples.petclinic.mcp.client.PetclinicApiClient;
import org.springframework.samples.petclinic.mcp.client.dto.Vet;
import org.springframework.stereotype.Component;

/** 獣医一覧の MCP ツール（{@code list_vets}）。 */
@Component
public class VetTools {

    private final PetclinicApiClient apiClient;

    public VetTools(PetclinicApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @McpTool(
        name = "list_vets",
        description = "獣医師一覧を診療科とともに取得します。",
        annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false)
    )
    public List<Vet> listVets() {
        return apiClient.listVets();
    }
}