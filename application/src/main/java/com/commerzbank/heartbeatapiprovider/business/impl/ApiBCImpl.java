package com.commerzbank.heartbeatapiprovider.business.impl;

import com.commerzbank.heartbeatapiprovider.business.ApiBC;
import com.commerzbank.heartbeatapiprovider.configuration.HeartbeatConfig;
import com.commerzbank.heartbeatapiprovider.dataccess.model.ApiInstance;
import com.commerzbank.heartbeatapiprovider.dataccess.platformApi.PlatformApiDAO;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ApiBCImpl implements ApiBC {

    private final PlatformApiDAO platformApiDAO;
    private final HeartbeatConfig beatConfig;

    // info
    private int backendUrls = 0;
    private int gatewayUrls = 0;


    public Mono<Tuple2<String, String>> loadApiUrls () {
        UUID uuid = UUID.randomUUID();
        log.info("Using Coba-ActivityId: " + uuid);


        // The builders store the urls of all APIs
        StringBuilder backendBuilder = new StringBuilder();
        StringBuilder gatewayBuilder = new StringBuilder();

        // Load APIs and extract the URLs
        return this.platformApiDAO.loadApiDetails(uuid.toString())
                .doOnNext(api -> extractUrlsFromApiInstance(api, backendBuilder, gatewayBuilder))
                .collectList()
                .map(apis -> new Tuple2<String, String>(backendBuilder.toString(), gatewayBuilder.toString()))
                .doOnNext(urlTuples -> {
                    log.info(String.format("Loaded %d Backend-URLs", backendUrls));
                    log.info(String.format("Loaded %d Gateway-URLs", gatewayUrls));


                    if (urlTuples._1 == null || urlTuples._1.isBlank() ||
                        urlTuples._2 == null || urlTuples._2.isBlank()) {
                        throw new RuntimeException("Could not load a single backend-url or gateway-url");
                    }
                });
    }


    private void extractUrlsFromApiInstance (ApiInstance api, StringBuilder backendBuilder, StringBuilder gatewayBuilder) {
        if (api.getBackendUrl() != null) {
            backendUrls++;
            backendBuilder.append(String.format(beatConfig.getUrlFormat(),
                    api.getBackendUrl() + beatConfig.getHealthPath()));
        }

        gatewayUrls++;
        gatewayBuilder.append(String.format(beatConfig.getUrlFormat(),
                beatConfig.getGatewayBasePath() + api.getBaseUrl() + beatConfig.getHealthPath()));
    }
}
