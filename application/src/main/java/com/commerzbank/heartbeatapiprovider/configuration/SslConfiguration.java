package com.commerzbank.heartbeatapiprovider.configuration;

import com.commerzbank.heartbeatapiprovider.utils.KeyStoreUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class SslConfiguration {
    private final Environment env;

    /**
     * TODO: Ugly, This should be done in another way.
     * Der KeyCloak client braucht das hier...
     */
    @PostConstruct
    @Profile("openshift")
    private void configureSSL() {
        String trustStore = env.getProperty("client.ssl.trust-store");
        if (trustStore != null) {
            System.setProperty("javax.net.ssl.trustStore", trustStore);
            System.setProperty("javax.net.ssl.trustStoreType", KeyStoreUtils.storeTypeByExtension(trustStore, "PKCS12"));
        }
        String trustStorePassword = env.getProperty("client.ssl.trust-store-password");
        if (trustStorePassword != null) {
            System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
        }
    }


}
