package org.springframework.samples.petclinic.mcp.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient petclinicRestClient(PetclinicApiProperties properties) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(properties.getTimeout());

        RestClient.Builder builder = RestClient.builder()
            .baseUrl(properties.getBaseUrl())
            .requestFactory(requestFactory);

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