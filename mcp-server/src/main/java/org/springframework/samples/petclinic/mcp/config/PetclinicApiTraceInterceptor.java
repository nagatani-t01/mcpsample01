package org.springframework.samples.petclinic.mcp.config;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

/**
 * 既存 Petclinic REST API への {@link org.springframework.web.client.RestClient} 呼び出しを
 * {@code [mcp-trace] outbound-api} として記録する（メソッド・URI・HTTP ステータス・所要時間）。
 */
@Component
public class PetclinicApiTraceInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(PetclinicApiTraceInterceptor.class);

    private final McpTraceProperties traceProperties;

    public PetclinicApiTraceInterceptor(McpTraceProperties traceProperties) {
        this.traceProperties = traceProperties;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        if (!traceProperties.isTraceEnabled()) {
            return execution.execute(request, body);
        }
        URI uri = request.getURI();
        long start = System.currentTimeMillis();
        log.info("[mcp-trace] outbound-api phase=start method={} uri={}", request.getMethod(), uri);
        ClientHttpResponse response = execution.execute(request, body);
        log.info(
            "[mcp-trace] outbound-api phase=end method={} uri={} status={} durationMs={}",
            request.getMethod(),
            uri,
            response.getStatusCode().value(),
            System.currentTimeMillis() - start
        );
        return response;
    }
}