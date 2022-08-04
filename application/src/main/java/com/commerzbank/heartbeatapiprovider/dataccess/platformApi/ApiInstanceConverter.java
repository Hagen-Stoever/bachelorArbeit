package com.commerzbank.heartbeatapiprovider.dataccess.platformApi;

import com.commerzbank.heartbeatapiprovider.dataaccess.platformApi.model.PlatformApiApiDTO;
import com.commerzbank.heartbeatapiprovider.dataccess.model.ApiInstance;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class ApiInstanceConverter {


    public ApiInstance convert (PlatformApiApiDTO dto) {
        URI backend = null;
        if (dto.getBackend() != null) {
            backend = dto.getBackend().getUri();
        }

        return new ApiInstance(
                dto.getId(),
                dto.getName(),
                dto.getBasePath(),
                backend
        );
    }
}
