package com.commerzbank.heartbeatapiprovider.dataccess.tickets.impl;

import com.commerzbank.heartbeatapiprovider.configuration.ResourceServer;
import com.commerzbank.heartbeatapiprovider.dataccess.model.Ticket;
import com.commerzbank.heartbeatapiprovider.dataccess.model.TicketDTO;
import com.commerzbank.heartbeatapiprovider.dataccess.tickets.TicketDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Slf4j
@Component
public class TicketDAOImpl implements TicketDAO {

    private static Ticket currentTicket;

    private final ResourceServer resourceServer;

    private final WebClient keycloakWebClient;


    public Mono<String> loadJWT () {
        if (currentTicket != null) {
            if   (!currentTicket.isExpired()) { return Mono.just(currentTicket.getAccessToken()); }
            else                              { return this.loadNewTicket(); }
        } else {
            return this.loadNewTicket();
        }
    }


    private Mono<String> loadNewTicket () {
        String body = String.format("grant_type=password&client_id=%s&client_secret=%s&username=%s",
                this.resourceServer.getClientId(),
                this.resourceServer.getSecret(),
                this.resourceServer.getTechUsername()
        );

        log.info("Requesting new Token from " + this.resourceServer.getBasePath() + this.resourceServer.getAccessTokenPath());

        return this.keycloakWebClient.post()
                .uri(this.resourceServer.getAccessTokenPath())
                .body(BodyInserters.fromValue(body))
                .retrieve()
                // Response
                .bodyToMono(TicketDTO.class)
                .map(Ticket::new)
                .doOnNext(newTicket -> { currentTicket = newTicket; })
                .doOnNext(newTicket -> { log.info("Received new Token"); })
                .map(Ticket::getAccessToken)

                .onErrorMap(WebClientResponseException.class, error -> {
                    log.error("Error while loading a Token: " + error.getResponseBodyAsString());
                    throw error;
                });
    }
}
