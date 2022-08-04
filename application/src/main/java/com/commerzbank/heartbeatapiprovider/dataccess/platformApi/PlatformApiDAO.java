package com.commerzbank.heartbeatapiprovider.dataccess.platformApi;

import com.commerzbank.heartbeatapiprovider.dataccess.model.ApiInstance;
import reactor.core.publisher.Flux;


public interface PlatformApiDAO {

    Flux<ApiInstance> loadApiDetails (String activityId);

}
