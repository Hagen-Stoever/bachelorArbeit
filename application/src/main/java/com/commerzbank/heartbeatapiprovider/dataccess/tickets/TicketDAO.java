package com.commerzbank.heartbeatapiprovider.dataccess.tickets;


import reactor.core.publisher.Mono;

public interface TicketDAO {

    Mono<String> loadJWT ();

}
