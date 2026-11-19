package org.springframework.samples.petclinic.mcp.config;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * {@code Origin} ヘッダーが許可リスト外の場合は 403 を返す（ブラウザ経由の不正呼び出し対策）。
 * ヘッダーが無いリクエスト（Cursor などのネイティブ MCP クライアント）は通す。
 */
@Component
@Order(0)
public class OriginValidationFilter extends OncePerRequestFilter {

    private final Set<String> allowedOrigins;

    public OriginValidationFilter(McpServerProperties properties) {
        this.allowedOrigins = properties.allowedOriginSet();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        if (origin != null && !origin.isBlank() && !allowedOrigins.contains(origin)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Origin not allowed");
            return;
        }
        filterChain.doFilter(request, response);
    }
}