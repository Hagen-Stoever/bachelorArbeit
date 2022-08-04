package com.commerzbank.heartbeatapiprovider.configuration;

import nl.altindag.ssl.SSLFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ClientSslConfiguration {

    @Bean
    @Scope("prototype")
    public SSLFactory clientSslFactory(ClientSslProperties   clientSslProperties) {
        return SSLFactory.builder()
                .withTrustMaterial(clientSslProperties.getTrustStorePath(), clientSslProperties.getTrustStorePassword(), "PKCS12")
                .withIdentityMaterial(clientSslProperties.getKeyStorePath(), clientSslProperties.getKeyStorePassword(), "PKCS12")
//                .withProtocol(KeyManagerFactory.getDefaultAlgorithm()) // TODO this is what the netty sdk did, but I think the builders default "TLSv1.2" is actually better. Opinions?
                .build();
    }
}
