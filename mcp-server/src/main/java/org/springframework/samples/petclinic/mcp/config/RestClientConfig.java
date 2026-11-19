package org.springframework.samples.petclinic.mcp.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * 既存 API 用と OpenAPI 取得用の {@link org.springframework.web.client.RestClient} Bean を定義する。
 * API 用クライアントには {@link PetclinicApiTraceInterceptor} を付与する。
 */
@Configuration
public class RestClientConfig {

    @Bean
    RestClient petclinicRestClient(PetclinicApiProperties properties, PetclinicApiTraceInterceptor traceInterceptor) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(properties.getTimeout());

        RestClient.Builder builder = RestClient.builder()
            .baseUrl(properties.getBaseUrl())
            .requestFactory(requestFactory)
            .requestInterceptor(traceInterceptor);

        if (properties.getUsername() != null && !properties.getUsername().isBlank()) {
            String token = Base64.getEncoder()
                .encodeToString((properties.getUsername() + ":" + properties.getPassword())
                    .getBytes(StandardCharsets.UTF_8));
            builder.defaultHeader("Authorization", "Basic " + token);
        }

        return builder.build();
    }

    @Bean
    RestClient openApiRestClient(PetclinicApiProperties properties) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(properties.getTimeout());
        return RestClient.builder().requestFactory(requestFactory).build();
    }
}