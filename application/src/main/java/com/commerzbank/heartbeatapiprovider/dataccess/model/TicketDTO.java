package com.commerzbank.heartbeatapiprovider.dataccess.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {

    private String access_token;

    private String refresh_token;

    private int expires_in;

    private int refresh_expires_in;

}
