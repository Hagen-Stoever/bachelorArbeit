package com.commerzbank.heartbeatapiprovider.configuration;

import com.commerzbank.heartbeatapiprovider.connection.LegacyWebClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {

    private final LegacyWebClientFactory webClientFactory;
    private final PlatformApiConfig platformApiConfig;

    @Bean
    public WebClient apiWebClient() {
        return webClientFactory.createWebClient(platformApiConfig.getWebClientTimeout());
    }
}
