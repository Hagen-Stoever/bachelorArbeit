package com.commerzbank.heartbeatapiprovider.business.impl;

import com.commerzbank.heartbeatapiprovider.configuration.HeartbeatConfig;
import com.commerzbank.heartbeatapiprovider.dataccess.platformApi.PlatformApiDAO;
import io.vavr.Tuple2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import reactor.core.publisher.Flux;
import ressources.TestData;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiBCImplTest {

    @Mock
    private PlatformApiDAO platformApiDAO;
    @Mock
    private HeartbeatConfig heartbeatConfig;


    private ApiBCImpl uut;

    private TestData data;


    @BeforeEach
    void setUp () {
        data = new TestData();
        heartbeatConfig = data.HEARTBEAT_CONFIG;
        platformApiDAO = mock(PlatformApiDAO.class);

        uut = new ApiBCImpl(platformApiDAO, heartbeatConfig);
    }


    @Test
    void Test_loadApiUrls_Ok ()  {
        // when
        when(platformApiDAO.loadApiDetails(anyString())).thenReturn(Flux.fromIterable(data.ALL_INSTANCES));

        // then
        Tuple2<String, String> result = uut.loadApiUrls().block();

        // then
        Assertions.assertTrue(result._1.contains(data.API_INSTANCE_OK.getBackendUrl().toString()));
        Assertions.assertTrue(result._1.contains(data.API_INSTANCE_OK2.getBackendUrl().toString()));

        Assertions.assertTrue(result._2.contains(data.API_INSTANCE_OK.getBaseUrl()));
        Assertions.assertTrue(result._2.contains(data.API_INSTANCE_OK2.getBaseUrl()));
        Assertions.assertTrue(result._2.contains(data.API_INSTANCE_MISSING_BACKEND.getBaseUrl()));
        Assertions.assertTrue(result._2.contains(data.API_INSTANCE_MISSING_BACKEND2.getBaseUrl()));

        Assertions.assertFalse(result._2.contains(data.API_INSTANCE_OK.getBackendUrl().toString()));
        Assertions.assertFalse(result._2.contains(data.API_INSTANCE_OK2.getBackendUrl().toString()));
    }

    @Test
    void Test_loadApiUrls_NoInstancesFromDao_Ok ()  {
        // when
        when(platformApiDAO.loadApiDetails(anyString())).thenReturn(Flux.fromIterable(data.ALL_INSTANCES_EMPTY));

        // then
        RuntimeException result = Assertions.assertThrows(RuntimeException.class, () -> uut.loadApiUrls().block());

        // then
        assertThat(result.getMessage(), equalTo("Could not load a single backend-url or gateway-url"));
    }


    @Test
    void Test_createConfigFiles_DaoThrowsException_Error () {
        // when
        when(platformApiDAO.loadApiDetails(anyString())).thenThrow(new RuntimeException("Some Error"));

        // then
        RuntimeException result = Assertions.assertThrows(RuntimeException.class, () -> uut.loadApiUrls().block());

        // then
        assertThat(result.getMessage(), equalTo("Some Error"));
    }

    @Test
    void Test_createConfigFiles_NoBackendUrls_Error () {
        // when
        when(platformApiDAO.loadApiDetails(anyString())).thenReturn(Flux.fromIterable(data.ALL_INSTANCES_MISSING_BACKENDS));

        // then
        RuntimeException result = Assertions.assertThrows(RuntimeException.class, () -> uut.loadApiUrls().block());

        // then
        assertThat(result.getMessage(), equalTo("Could not load a single backend-url or gateway-url"));
    }
}
