package com.commerzbank.heartbeatapiprovider.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("resource-server")
public class ResourceServer {

    private Duration webClientTimeout;

    private String basePath;

    private String accessTokenPath;

    private String clientId;

    private String secret;

    private String techUsername;

    private String techUserPassword;

}
