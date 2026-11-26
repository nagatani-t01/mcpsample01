package org.springframework.samples.petclinic.mcp.privacy;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * {@code Mcp-Session-Id} を {@link McpSessionContext} に載せ、同意状態をセッション単位で扱う。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class McpSessionIdFilter extends OncePerRequestFilter {

    public static final String MCP_SESSION_HEADER = "Mcp-Session-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        McpSessionContext.set(request.getHeader(MCP_SESSION_HEADER));
        try {
            filterChain.doFilter(request, response);
        }
        finally {
            McpSessionContext.clear();
        }
    }
}
