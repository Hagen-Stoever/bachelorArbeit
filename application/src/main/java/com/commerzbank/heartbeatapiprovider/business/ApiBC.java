package com.commerzbank.heartbeatapiprovider.business;

import io.vavr.Tuple2;
import reactor.core.publisher.Mono;

public interface ApiBC {

    /**
     * Loads apis and extracts the urls from it and groups the urls into one string (one for backend, one for gateway)
     * @return left-tuple: backend-urls   right-tuple: gateway-urls
     * @throws Exception Any Exception that occurred while retrieving the data
     */
    Mono<Tuple2<String, String>> loadApiUrls() throws Exception;

}
