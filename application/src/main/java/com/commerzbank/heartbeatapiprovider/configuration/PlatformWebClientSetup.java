package com.commerzbank.heartbeatapiprovider.configuration;

import com.commerzbank.heartbeatapiprovider.dataaccess.platformApi.ApiClient;
import com.commerzbank.heartbeatapiprovider.dataaccess.platformApi.RFC3339DateFormat;
import com.commerzbank.heartbeatapiprovider.dataaccess.platformApi.impl.ApiInformationApi;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.DateFormat;
import java.util.TimeZone;

@Data
@NoArgsConstructor
@Builder
@Configuration
public class PlatformWebClientSetup {

    @Bean
    public ApiInformationApi apiInformationApi (WebClient apiWebClient, PlatformApiConfig config) {
        DateFormat dateFormat = new RFC3339DateFormat();
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        ApiClient apiClient = new ApiClient(apiWebClient, null, dateFormat);
        apiClient.setBasePath(config.getBaseUrl());
        return new ApiInformationApi(apiClient);
    }
}
