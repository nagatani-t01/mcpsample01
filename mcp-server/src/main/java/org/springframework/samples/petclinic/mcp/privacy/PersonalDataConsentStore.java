package org.springframework.samples.petclinic.mcp.privacy;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * MCP セッションごとの個人情報利用同意。プロセス内メモリのみ（再起動で消える）。
 */
@Component
public class PersonalDataConsentStore {

    private final ConcurrentHashMap<String, Boolean> consentBySession = new ConcurrentHashMap<>();

    public boolean hasConsent() {
        return Boolean.TRUE.equals(consentBySession.get(McpSessionContext.get()));
    }

    public void grant() {
        consentBySession.put(McpSessionContext.get(), Boolean.TRUE);
    }

    public void revoke() {
        consentBySession.remove(McpSessionContext.get());
    }
}
