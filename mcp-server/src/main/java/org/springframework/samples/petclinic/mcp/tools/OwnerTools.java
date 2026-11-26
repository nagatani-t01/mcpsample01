package org.springframework.samples.petclinic.mcp.tools;

import java.util.List;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.samples.petclinic.mcp.client.PetclinicApiClient;
import org.springframework.samples.petclinic.mcp.client.dto.Owner;
import org.springframework.samples.petclinic.mcp.client.dto.OwnerFields;
import org.springframework.stereotype.Component;

/**
 * 飼い主の MCP ツール（{@code list_owners} / {@code get_owner} / {@code create_owner}）。
 * 業務ロジックは持たず {@link org.springframework.samples.petclinic.mcp.client.PetclinicApiClient} に委譲する。
 */
@Component
public class OwnerTools {

    private final PetclinicApiClient apiClient;

    public OwnerTools(PetclinicApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @McpTool(
        name = "list_owners",
        description = """
            飼い主を検索します。lastName を指定すると姓の前方一致で絞り込みます（省略時は全件）。
            個人情報を含むため、ユーザー同意後に grant_personal_data_consent を実行済みであること。""",
        annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false)
    )
    public List<Owner> listOwners(
        @McpToolParam(description = "姓（前方一致）。省略可", required = false) String lastName
    ) {
        return apiClient.listOwners(lastName);
    }

    @McpTool(
        name = "get_owner",
        description = """
            飼い主 ID を指定して、ペットと来院記録を含む詳細を取得します。
            個人情報を含むため、ユーザー同意後に grant_personal_data_consent を実行済みであること。""",
        annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false)
    )
    public Owner getOwner(
        @McpToolParam(description = "飼い主 ID", required = true) int ownerId
    ) {
        return apiClient.getOwner(ownerId);
    }

    @McpTool(
        name = "create_owner",
        description = """
            飼い主を新規登録します。firstName/lastName は英字のみ 1〜30 文字、address は 1〜255 文字、
            city は 1〜80 文字、telephone は数字のみ 1〜20 文字です。""",
        annotations = @McpTool.McpAnnotations(readOnlyHint = false, destructiveHint = false)
    )
    public Owner createOwner(
        @McpToolParam(description = "名（英字）", required = true) String firstName,
        @McpToolParam(description = "姓（英字）", required = true) String lastName,
        @McpToolParam(description = "住所", required = true) String address,
        @McpToolParam(description = "都市", required = true) String city,
        @McpToolParam(description = "電話番号（数字のみ）", required = true) String telephone
    ) {
        return apiClient.createOwner(new OwnerFields(firstName, lastName, address, city, telephone));
    }
}