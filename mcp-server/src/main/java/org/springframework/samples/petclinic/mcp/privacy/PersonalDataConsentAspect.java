package org.springframework.samples.petclinic.mcp.privacy;

import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 個人情報を含むツール結果を、同意が無ければ仮名化して返す（mcp-server-conceal 相当）。
 * {@code masking-enabled=false} のときは従来どおり同意要求例外で遮断する。
 */
@Aspect
@Component
@Order(200)
public class PersonalDataConsentAspect {

    private static final Logger log = LoggerFactory.getLogger(PersonalDataConsentAspect.class);

    private static final Set<String> CONSENT_TOOLS = Set.of(
        "grant_personal_data_consent",
        "revoke_personal_data_consent",
        "get_personal_data_consent_status"
    );

    private final PersonalDataConsentProperties properties;

    private final PersonalDataConsentStore consentStore;

    private final PersonalDataDetector detector;

    private final PersonalDataMasker masker;

    public PersonalDataConsentAspect(
        PersonalDataConsentProperties properties,
        PersonalDataConsentStore consentStore,
        PersonalDataDetector detector,
        PersonalDataMasker masker
    ) {
        this.properties = properties;
        this.consentStore = consentStore;
        this.detector = detector;
        this.masker = masker;
    }

    @Around("@annotation(mcpTool)")
    public Object gatePersonalData(ProceedingJoinPoint joinPoint, McpTool mcpTool) throws Throwable {
        if (!properties.isConsentRequired()) {
            return joinPoint.proceed();
        }
        String toolName = resolveToolName(joinPoint, mcpTool);
        if (CONSENT_TOOLS.contains(toolName)) {
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();
        if (!detector.containsPersonalData(result) || consentStore.hasConsent()) {
            return result;
        }

        if (properties.isMaskingEnabled()) {
            // メソッド戻り値型（Owner 等）を崩さないよう、同じ型の仮名化オブジェクトだけ返す
            Object masked = masker.mask(result);
            log.info(
                "[mcp-trace] personal-data phase=masked name={} session={}",
                toolName,
                McpSessionContext.get()
            );
            return masked;
        }

        log.info(
            "[mcp-trace] personal-data phase=blocked name={} session={} reason=pii_detected_in_response",
            toolName,
            McpSessionContext.get()
        );
        throw new PersonalDataConsentRequiredException();
    }

    private static String resolveToolName(ProceedingJoinPoint joinPoint, McpTool mcpTool) {
        if (mcpTool.name() != null && !mcpTool.name().isBlank()) {
            return mcpTool.name();
        }
        return joinPoint.getSignature().getName();
    }
}
