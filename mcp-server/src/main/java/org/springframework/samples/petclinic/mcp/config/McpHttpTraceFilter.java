package org.springframework.samples.petclinic.mcp.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Cursor などからの MCP 用 HTTP（URI に {@code /mcp} を含む）の入出力を {@code [mcp-trace] inbound} として記録する。
 * JSON-RPC 本文はログに出さない（メソッド・パス・ステータスのみ）。
 */
@Component
@Order(1)
public class McpHttpTraceFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(McpHttpTraceFilter.class);

    private final McpTraceProperties traceProperties;

    public McpHttpTraceFilter(McpTraceProperties traceProperties) {
        this.traceProperties = traceProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (!traceProperties.isTraceEnabled() || !isMcpTraffic(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        long start = System.currentTimeMillis();
        String method = request.getMethod();
        String path = request.getRequestURI();
        String origin = request.getHeader("Origin");
        log.info("[mcp-trace] inbound phase=start method={} path={} origin={}", method, path, origin != null ? origin : "-");
        try {
            filterChain.doFilter(request, response);
        }
        finally {
            long durationMs = System.currentTimeMillis() - start;
            log.info(
                "[mcp-trace] inbound phase=end method={} path={} status={} durationMs={}",
                method,
                path,
                response.getStatus(),
                durationMs
            );
        }
    }

    private static boolean isMcpTraffic(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && uri.contains("/mcp");
    }
}