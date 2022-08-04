package com.commerzbank.heartbeatapiprovider.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConfigurationProperties("heart-beat")
@Component
public class HeartbeatConfig {

    private String configFilePath;

    private String healthPath;

    private String gatewayBasePath;

    private String urlFormat;

    private Monitor backendMonitor;

    private Monitor gatewayMonitor;


    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Monitor {

        private String name;

        private String id; //

        private String schedule;

        private String fileName;

    }
}
