package com.commerzbank.heartbeatapiprovider.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@RequiredArgsConstructor
@Component
public class ConfigValidation {

    private final HeartbeatConfig heartbeatConfig;
    private final PlatformApiConfig platformApiConfig;

    public void checkConfigs () throws RuntimeException {
        ArrayList<String> missingConfigs = new ArrayList();

        if (heartbeatConfig.getConfigFilePath() == null) {
            missingConfigs.add("config-file-path");
        }
        if (heartbeatConfig.getGatewayBasePath() == null) {
            missingConfigs.add("gateway-base-path");
        }
        if (heartbeatConfig.getHealthPath() == null) {
            missingConfigs.add("config-file-path");
        }
        if (heartbeatConfig.getBackendMonitor() == null) {
            missingConfigs.add("backend-monitor");
        } else {
            if (heartbeatConfig.getBackendMonitor().getFileName() == null) {
                missingConfigs.add("backend-monitor: file-name");
            }
            if (heartbeatConfig.getBackendMonitor().getSchedule() == null) {
                missingConfigs.add("backend-monitor: schedule");
            }
            if (heartbeatConfig.getBackendMonitor().getId() == null) {
                missingConfigs.add("backend-monitor: id");
            }
            if (heartbeatConfig.getBackendMonitor().getName() == null) {
                missingConfigs.add("backend-monitor: name");
            }
        }

        if (heartbeatConfig.getGatewayMonitor() == null) {
            missingConfigs.add("gateway-monitor");
        } else {
            if (heartbeatConfig.getGatewayMonitor().getFileName() == null) {
                missingConfigs.add("gateway-monitor: file-name");
            }
            if (heartbeatConfig.getGatewayMonitor().getSchedule() == null) {
                missingConfigs.add("gateway-monitor: schedule");
            }
            if (heartbeatConfig.getGatewayMonitor().getId() == null) {
                missingConfigs.add("gateway-monitor: id");
            }
            if (heartbeatConfig.getGatewayMonitor().getName() == null) {
                missingConfigs.add("gateway-monitor: name");
            }
        }


        if (platformApiConfig.getBaseUrl() == null) {
            missingConfigs.add("platform-api: base-url");
        }



        if (missingConfigs.size() == 1) {
            throw new RuntimeException(String.format("The Parameter %s is not set!", missingConfigs.get(0)));
        } else if (missingConfigs.size() > 1) {
            StringBuilder args = new StringBuilder(missingConfigs.get(0));

            for (String arg : missingConfigs) {
                args.append(arg);
            }

            throw new RuntimeException(String.format("The Parameters %s are not set!", args));
        }
    }
}
