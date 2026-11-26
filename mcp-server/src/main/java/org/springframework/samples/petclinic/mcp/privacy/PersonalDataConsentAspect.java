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
 * {@code @McpTool} の戻り値に個人情報が含まれる場合、同意が無ければ結果を破棄して例外にする。
 * 同意ツール自体は対象外。トレース Aspect より内側で動かし、成功ログに実データを残さない。
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

    /** 個人情報を返すことが分かっているツール。同意が無ければ API 呼び出し前に止める。 */
    private static final Set<String> KNOWN_PERSONAL_DATA_TOOLS = Set.of(
        "list_owners",
        "get_owner",
        "create_owner",
        "list_pets",
        "create_pet",
        "list_vets",
        "list_visits",
        "create_visit"
    );

    private final PersonalDataConsentProperties properties;

    private final PersonalDataConsentStore consentStore;

    private final PersonalDataDetector detector;

    public PersonalDataConsentAspect(
        PersonalDataConsentProperties properties,
        PersonalDataConsentStore consentStore,
        PersonalDataDetector detector
    ) {
        this.properties = properties;
        this.consentStore = consentStore;
        this.detector = detector;
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
        if (KNOWN_PERSONAL_DATA_TOOLS.contains(toolName) && !consentStore.hasConsent()) {
            log.info(
                "[mcp-trace] personal-data phase=blocked name={} session={} reason=consent_required_before_call",
                toolName,
                McpSessionContext.get()
            );
            throw new PersonalDataConsentRequiredException();
        }
        Object result = joinPoint.proceed();
        if (detector.containsPersonalData(result) && !consentStore.hasConsent()) {
            log.info(
                "[mcp-trace] personal-data phase=blocked name={} session={} reason=pii_detected_in_response",
                toolName,
                McpSessionContext.get()
            );
            throw new PersonalDataConsentRequiredException();
        }
        return result;
    }

    private static String resolveToolName(ProceedingJoinPoint joinPoint, McpTool mcpTool) {
        if (mcpTool.name() != null && !mcpTool.name().isBlank()) {
            return mcpTool.name();
        }
        return joinPoint.getSignature().getName();
    }
}
