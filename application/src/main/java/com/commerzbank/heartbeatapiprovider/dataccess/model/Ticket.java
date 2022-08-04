package com.commerzbank.heartbeatapiprovider.dataccess.model;

import lombok.Value;

import java.time.LocalTime;

@Value
public class Ticket {

    private static final long TOKEN_EXPIRATION_BUFFER = 10;

    private String accessToken;

    private String refreshToken;

    private int durationAccessToken;

    private int durationRefreshToken;

    private LocalTime creationTime;


    public Ticket(TicketDTO dto) {
        this.accessToken = dto.getAccess_token();
        this.refreshToken = dto.getRefresh_token();
        this.durationAccessToken = dto.getExpires_in();
        this.durationRefreshToken = dto.getRefresh_expires_in();
        this.creationTime = LocalTime.now();
    }


    public boolean isExpired () {
        return creationTime.plusSeconds(durationAccessToken - TOKEN_EXPIRATION_BUFFER).isBefore(LocalTime.now());
    }


    public boolean isRefreshExpired () {
        return creationTime.plusSeconds(durationRefreshToken - TOKEN_EXPIRATION_BUFFER).isBefore(LocalTime.now());
    }
}
