package org.springframework.samples.petclinic.mcp.privacy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.samples.petclinic.mcp.client.dto.Owner;

class PersonalDataConsentAspectTests {

    private final PersonalDataConsentProperties properties = new PersonalDataConsentProperties();

    private final PersonalDataConsentStore store = new PersonalDataConsentStore();

    private final PersonalDataDetector detector = new PersonalDataDetector();

    private final PseudonymDictionary dictionary = new PseudonymDictionary();

    private final PersonalDataMasker masker = new PersonalDataMasker(dictionary);

    private final PersonalDataConsentAspect aspect =
        new PersonalDataConsentAspect(properties, store, detector, masker);

    @BeforeEach
    void setSession() {
        McpSessionContext.set("test-session");
        store.revoke();
        dictionary.clearCurrentSession();
        properties.setConsentRequired(true);
        properties.setMaskingEnabled(true);
    }

    @AfterEach
    void clear() {
        McpSessionContext.clear();
    }

    @Test
    void masksPersonalDataWithoutConsent() throws Throwable {
        Owner owner = new Owner(1, "George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023", List.of());
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.proceed()).thenReturn(owner);
        McpTool tool = tool("get_owner");

        Object result = aspect.gatePersonalData(pjp, tool);

        assertThat(result).isInstanceOf(Owner.class);
        Owner masked = (Owner) result;
        assertThat(masked.id()).isEqualTo(1);
        assertThat(masked.firstName()).isNotEqualTo("George");
        assertThat(masked.lastName()).isNotEqualTo("Franklin");
        assertThat(masked.telephone()).isNotEqualTo("6085551023");
    }

    @Test
    void allowsAfterConsent() throws Throwable {
        store.grant();
        Owner owner = new Owner(1, "A", "B", "addr", "city", "123", List.of());
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.proceed()).thenReturn(owner);
        McpTool tool = tool("get_owner");

        assertThat(aspect.gatePersonalData(pjp, tool)).isSameAs(owner);
    }

    @Test
    void blocksWhenMaskingDisabled() throws Throwable {
        properties.setMaskingEnabled(false);
        Owner owner = new Owner(1, "A", "B", "addr", "city", "123", List.of());
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.proceed()).thenReturn(owner);
        McpTool tool = tool("some_custom_tool");

        assertThatThrownBy(() -> aspect.gatePersonalData(pjp, tool))
            .isInstanceOf(PersonalDataConsentRequiredException.class);
    }

    @Test
    void skipsGateWhenDisabled() throws Throwable {
        properties.setConsentRequired(false);
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.proceed()).thenReturn("ok");
        McpTool tool = tool("get_owner");

        assertThat(aspect.gatePersonalData(pjp, tool)).isEqualTo("ok");
    }

    private static McpTool tool(String name) {
        McpTool tool = mock(McpTool.class);
        when(tool.name()).thenReturn(name);
        return tool;
    }
}
