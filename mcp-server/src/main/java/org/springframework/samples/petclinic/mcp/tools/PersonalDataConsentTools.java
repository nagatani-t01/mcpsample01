package org.springframework.samples.petclinic.mcp.tools;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.samples.petclinic.mcp.client.PetclinicApiException;
import org.springframework.samples.petclinic.mcp.privacy.McpSessionContext;
import org.springframework.samples.petclinic.mcp.privacy.PersonalDataConsentStore;
import org.springframework.stereotype.Component;

/**
 * 個人情報を含むツール結果を返す前の、ユーザー同意の登録・確認・撤回。
 */
@Component
public class PersonalDataConsentTools {

    private final PersonalDataConsentStore consentStore;

    public PersonalDataConsentTools(PersonalDataConsentStore consentStore) {
        this.consentStore = consentStore;
    }

    @McpTool(
        name = "grant_personal_data_consent",
        description = """
            ユーザーが個人情報（飼い主の氏名・住所・電話、ペット情報、獣医名など）の取得・表示に同意したあとでだけ実行します。
            同意を得ていないのに confirmed=true で呼び出してはいけません。""",
        annotations = @McpTool.McpAnnotations(readOnlyHint = false, destructiveHint = false)
    )
    public Map<String, Object> grantPersonalDataConsent(
        @McpToolParam(
            description = "ユーザーが明示的に同意した場合のみ true。同意が無い・不明な場合は false のままにすること。",
            required = true
        ) boolean confirmed
    ) {
        if (!confirmed) {
            throw new PetclinicApiException(
                "confirmed=false のため同意を登録しませんでした。ユーザーの明示的な同意があるときだけ confirmed=true で再実行してください。"
            );
        }
        consentStore.grant();
        return statusPayload("granted", "個人情報の取得・表示について、この MCP セッションで同意を記録しました。");
    }

    @McpTool(
        name = "revoke_personal_data_consent",
        description = "個人情報利用の同意を取り消します。以降、個人情報を含むツールは同意なしでは結果を返しません。",
        annotations = @McpTool.McpAnnotations(readOnlyHint = false, destructiveHint = false)
    )
    public Map<String, Object> revokePersonalDataConsent() {
        consentStore.revoke();
        return statusPayload("revoked", "個人情報利用の同意を取り消しました。");
    }

    @McpTool(
        name = "get_personal_data_consent_status",
        description = "現在の MCP セッションで個人情報利用の同意があるかを返します。",
        annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false)
    )
    public Map<String, Object> getPersonalDataConsentStatus() {
        boolean consented = consentStore.hasConsent();
        return statusPayload(
            consented ? "granted" : "missing",
            consented ? "同意済みです。" : "同意がありません。個人情報を含む応答は返しません。"
        );
    }

    private static Map<String, Object> statusPayload(String status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status);
        body.put("consented", "granted".equals(status));
        body.put("sessionId", McpSessionContext.get());
        body.put("message", message);
        return body;
    }
}
