package org.springframework.samples.petclinic.mcp.privacy;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 個人情報を含む API 応答の同意ゲート設定。
 * {@code petclinic.mcp.privacy.consent-required=false} で無効化できる。
 */
@ConfigurationProperties(prefix = "petclinic.mcp.privacy")
public class PersonalDataConsentProperties {

    /**
     * When true, tool results that contain personal data are withheld until
     * {@code grant_personal_data_consent} has been called for the MCP session.
     */
    private boolean consentRequired = true;

    public boolean isConsentRequired() {
        return consentRequired;
    }

    public void setConsentRequired(boolean consentRequired) {
        this.consentRequired = consentRequired;
    }
}
