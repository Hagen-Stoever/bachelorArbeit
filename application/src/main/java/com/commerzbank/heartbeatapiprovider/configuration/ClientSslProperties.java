package com.commerzbank.heartbeatapiprovider.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConfigurationProperties("client.ssl")
public class ClientSslProperties {
    String keyStore;
    char[] keyStorePassword;
    String trustStore;
    char[] trustStorePassword;

    public Path getKeyStorePath() {
        return Paths.get(keyStore);
    }

    public Path getTrustStorePath() {
        return Paths.get(trustStore);
    }

}
