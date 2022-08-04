package com.commerzbank.heartbeatapiprovider.dataccess.tickets.config;

import com.commerzbank.heartbeatapiprovider.configuration.ResourceServer;
import com.commerzbank.heartbeatapiprovider.connection.LegacyWebClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class KeycloakConfiguration {

    private final LegacyWebClientFactory webClientFactory;

    private final ResourceServer resourceServer;

    @Bean
    public WebClient keycloakWebClient (ResourceServer server) {
        return webClientFactory.createWebClient(resourceServer.getWebClientTimeout())
                .mutate()
                .baseUrl(server.getBasePath())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .build();
    }
}
