package com.commerzbank.heartbeatapiprovider.dataccess.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.net.URI;
import java.util.UUID;

@Value
@AllArgsConstructor
public class ApiInstance {

    private UUID apiId; // Currently not used, but can be used in future for better error handling

    private String name; // Currently not used, but can be used in future for better error handling

    private String baseUrl;

    private URI backendUrl;

}
