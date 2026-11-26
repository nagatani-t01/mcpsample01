package org.springframework.samples.petclinic.mcp.config;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * {@code @McpTool} メソッドの前後で {@code [mcp-trace] tool} ログを出す。
 * ツール名・引数・所要時間・結果プレビュー（最大 500 文字）を記録し、手動テスト時の追跡に使う。
 * 個人情報ゲート（{@code PersonalDataConsentAspect}）より外側で動かし、同意拒否時は error ログのみにする。
 */
@Aspect
@Component
@Order(100)
public class McpToolTraceAspect {

    private static final Logger log = LoggerFactory.getLogger(McpToolTraceAspect.class);

    private static final int RESULT_PREVIEW_CHARS = 500;

    private final McpTraceProperties traceProperties;

    public McpToolTraceAspect(McpTraceProperties traceProperties) {
        this.traceProperties = traceProperties;
    }

    @Around("@annotation(mcpTool)")
    public Object traceTool(ProceedingJoinPoint joinPoint, McpTool mcpTool) throws Throwable {
        if (!traceProperties.isTraceEnabled()) {
            return joinPoint.proceed();
        }
        String toolName = mcpTool.name().isBlank()
            ? joinPoint.getSignature().getName()
            : mcpTool.name();
        String argsSummary = formatArgs(joinPoint);
        long start = System.currentTimeMillis();
        log.info("[mcp-trace] tool phase=start name={} args={}", toolName, argsSummary);
        try {
            Object result = joinPoint.proceed();
            log.info(
                "[mcp-trace] tool phase=success name={} durationMs={} resultPreview={}",
                toolName,
                System.currentTimeMillis() - start,
                preview(result)
            );
            return result;
        }
        catch (Throwable ex) {
            log.info(
                "[mcp-trace] tool phase=error name={} durationMs={} error={}",
                toolName,
                System.currentTimeMillis() - start,
                ex.getMessage()
            );
            throw ex;
        }
    }

    private static String formatArgs(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] names = signature.getParameterNames();
        Object[] values = joinPoint.getArgs();
        if (names == null || values == null) {
            return Arrays.toString(values);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < names.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(names[i]).append("=").append(values[i]);
        }
        return sb.toString();
    }

    private static String preview(Object result) {
        if (result == null) {
            return "null";
        }
        String text = String.valueOf(result);
        if (text.length() <= RESULT_PREVIEW_CHARS) {
            return text;
        }
        return text.substring(0, RESULT_PREVIEW_CHARS) + "…(truncated)";
    }
}