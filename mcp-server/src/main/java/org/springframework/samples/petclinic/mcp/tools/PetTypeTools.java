package org.springframework.samples.petclinic.mcp.tools;

import java.util.List;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.samples.petclinic.mcp.client.PetclinicApiClient;
import org.springframework.samples.petclinic.mcp.client.dto.PetType;
import org.springframework.stereotype.Component;

/** ペット種別一覧の MCP ツール（{@code list_pet_types}）。{@code create_pet} の種別 ID 確認にも使う。 */
@Component
public class PetTypeTools {

    private final PetclinicApiClient apiClient;

    public PetTypeTools(PetclinicApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @McpTool(
        name = "list_pet_types",
        description = "登録可能なペット種別一覧を取得します。",
        annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false)
    )
    public List<PetType> listPetTypes() {
        return apiClient.listPetTypes();
    }
}