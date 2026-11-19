package org.springframework.samples.petclinic.mcp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@code petclinic.mcp.trace-enabled} / 環境変数 {@code MCP_TRACE_ENABLED} によるトレースログの on/off。
 */
@ConfigurationProperties(prefix = "petclinic.mcp")
public class McpTraceProperties {

    /**
     * When true, emit {@code [mcp-trace]} log lines for MCP HTTP, tool calls, and Petclinic API requests.
     */
    private boolean traceEnabled = true;

    public boolean isTraceEnabled() {
        return traceEnabled;
    }

    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }
}