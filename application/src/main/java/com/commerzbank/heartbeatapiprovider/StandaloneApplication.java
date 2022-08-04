package com.commerzbank.heartbeatapiprovider;

import com.commerzbank.heartbeatapiprovider.configuration.ClientSslProperties;
import com.commerzbank.heartbeatapiprovider.configuration.ConfigValidation;
import com.commerzbank.heartbeatapiprovider.configuration.PlatformApiConfig;
import com.commerzbank.heartbeatapiprovider.configuration.ResourceServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties({
        ClientSslProperties.class,
        PlatformApiConfig.class,
        ResourceServer.class
})
@Slf4j
public class StandaloneApplication implements CommandLineRunner {

    @Autowired
    private ProgramController program;

    @Autowired
    private ConfigValidation validation;

    public static void main(String[] args) {
        SpringApplication.run(StandaloneApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            validation.checkConfigs();
            List<IOException> errors = program.runProgram().block();

            if (errors.size() == 1) {
                log.error("", errors.get(0));
                System.exit(1);
            } else if (errors.size() > 1) {
                log.error("", errors.get(0));
                System.exit(2);
            }
        } catch (Exception e) {
            log.error("Experienced an Error: ", e);
            System.exit(2);
        }
    }
}
