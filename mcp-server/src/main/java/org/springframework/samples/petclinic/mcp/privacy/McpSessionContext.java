package org.springframework.samples.petclinic.mcp.privacy;

/**
 * 現在の MCP HTTP リクエストのセッション ID（{@code Mcp-Session-Id}）を保持する。
 */
public final class McpSessionContext {

    private static final String ANONYMOUS = "anonymous";

    private static final ThreadLocal<String> SESSION_ID = new ThreadLocal<>();

    private McpSessionContext() {
    }

    public static void set(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            SESSION_ID.set(ANONYMOUS);
        }
        else {
            SESSION_ID.set(sessionId.trim());
        }
    }

    public static String get() {
        String id = SESSION_ID.get();
        return id != null ? id : ANONYMOUS;
    }

    public static void clear() {
        SESSION_ID.remove();
    }
}
