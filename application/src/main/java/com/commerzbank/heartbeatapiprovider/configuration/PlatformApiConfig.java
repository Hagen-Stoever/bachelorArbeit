package com.commerzbank.heartbeatapiprovider.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("platform-api")
@Data
public class PlatformApiConfig {

    private String baseUrl;

    private Duration webClientTimeout;

}
