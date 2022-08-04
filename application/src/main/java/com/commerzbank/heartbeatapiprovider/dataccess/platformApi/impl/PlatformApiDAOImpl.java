package com.commerzbank.heartbeatapiprovider.dataccess.platformApi.impl;

import com.commerzbank.heartbeatapiprovider.dataaccess.platformApi.impl.ApiInformationApi;
import com.commerzbank.heartbeatapiprovider.dataaccess.platformApi.model.PlatformApiApiDTO;
import com.commerzbank.heartbeatapiprovider.dataaccess.platformApi.model.PlatformApiApiReferenceDTO;
import com.commerzbank.heartbeatapiprovider.dataaccess.platformApi.model.PlatformApiInlineResponse200DTO;
import com.commerzbank.heartbeatapiprovider.dataccess.model.ApiInstance;
import com.commerzbank.heartbeatapiprovider.dataccess.platformApi.ApiInstanceConverter;
import com.commerzbank.heartbeatapiprovider.dataccess.platformApi.PlatformApiDAO;
import com.commerzbank.heartbeatapiprovider.dataccess.tickets.TicketDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class PlatformApiDAOImpl implements PlatformApiDAO {

    private final ApiInformationApi apiInformationApi;
    private final TicketDAO ticketDAO;
    private final ApiInstanceConverter converter;


    @Override
    public Flux<ApiInstance> loadApiDetails (String activityId) {
        return this.loadAllApiIDs(activityId)
                .flatMap(id -> loadApiDetail(activityId, id))
                .map(converter::convert);
    }


    private Flux<UUID> loadAllApiIDs (String activityId) {
        return this.ticketDAO.loadJWT()
                .doOnNext(ticket -> log.info("Requesting all APIs"))
                // Load all APIs
                .flatMap(ticket -> this.apiInformationApi.getApis(
                        "Bearer " + ticket,
                        activityId,
                        null)
                )
                .mapNotNull(PlatformApiInlineResponse200DTO::getApis)
                .retry(1)
                .cache()
                .doOnNext(apis -> log.info(String.format("Loaded %s APIs", apis.size())))
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(Mono.error(new RuntimeException("No APIs could be loaded")))
                .map(PlatformApiApiReferenceDTO::getId);
    }


    private Mono<PlatformApiApiDTO> loadApiDetail (String activityId, UUID id) {
        return this.ticketDAO.loadJWT()
                .flatMap(ticket -> this.apiInformationApi.getApi(
                        "Bearer " + ticket,
                        id,
                        activityId)
                )
                .retry(1)
                .onErrorResume(e -> {
                    System.out.println(e);
                    log.warn("Unable to retrieve info for api " + id);
                    return Mono.empty();
                });
    }
}
