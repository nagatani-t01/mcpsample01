package org.springframework.samples.petclinic.mcp.privacy;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 個人情報の同意と仮名化（conceal 相当）の設定。
 */
@ConfigurationProperties(prefix = "petclinic.mcp.privacy")
public class PersonalDataConsentProperties {

    /**
     * When true, personal data in tool results is protected until
     * {@code grant_personal_data_consent} is called for the MCP session.
     */
    private boolean consentRequired = true;

    /**
     * When true (and consent is missing), personal fields are pseudo-anonymized
     * instead of blocking the tool. IDs and structure are preserved.
     */
    private boolean maskingEnabled = true;

    public boolean isConsentRequired() {
        return consentRequired;
    }

    public void setConsentRequired(boolean consentRequired) {
        this.consentRequired = consentRequired;
    }

    public boolean isMaskingEnabled() {
        return maskingEnabled;
    }

    public void setMaskingEnabled(boolean maskingEnabled) {
        this.maskingEnabled = maskingEnabled;
    }
}
