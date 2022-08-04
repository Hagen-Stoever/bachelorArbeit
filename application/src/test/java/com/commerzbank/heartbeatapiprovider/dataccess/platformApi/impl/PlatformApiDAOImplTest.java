package com.commerzbank.heartbeatapiprovider.dataccess.platformApi.impl;

import com.commerzbank.heartbeatapiprovider.dataaccess.platformApi.impl.ApiInformationApi;
import com.commerzbank.heartbeatapiprovider.dataccess.model.ApiInstance;
import com.commerzbank.heartbeatapiprovider.dataccess.platformApi.ApiInstanceConverter;
import com.commerzbank.heartbeatapiprovider.dataccess.tickets.TicketDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestClientException;
import reactor.core.publisher.Mono;
import ressources.TestData;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlatformApiDAOImplTest {

    private ApiInformationApi apiInformationApi;
    @Mock
    private TicketDAO ticketDAO;

    private ApiInstanceConverter converter = new ApiInstanceConverter();


    private PlatformApiDAOImpl uut;

    private TestData data;


    @BeforeEach
    void setUp () {
        data = new TestData();
        ticketDAO = mock(TicketDAO.class);
        apiInformationApi = mock(ApiInformationApi.class);

        when(ticketDAO.loadJWT()).thenReturn(Mono.just(data.TOKEN));
        when(apiInformationApi.getApi("Bearer " + data.TOKEN, data.OK.getId(), data.COBA_ACTIVITY_ID)).thenReturn(Mono.just(data.OK));
        when(apiInformationApi.getApi("Bearer " + data.TOKEN, data.OK2.getId(), data.COBA_ACTIVITY_ID)).thenReturn(Mono.just(data.OK2));
        when(apiInformationApi.getApi("Bearer " + data.TOKEN, data.MISSING_BACKEND.getId(), data.COBA_ACTIVITY_ID)).thenReturn(Mono.just(data.MISSING_BACKEND));
        when(apiInformationApi.getApi("Bearer " + data.TOKEN, data.MISSING_BACKEND2.getId(), data.COBA_ACTIVITY_ID)).thenReturn(Mono.just(data.MISSING_BACKEND2));

        uut = new PlatformApiDAOImpl(apiInformationApi, ticketDAO, converter);
    }


    @Test
    public void Test_loadApiDetails_NoApis_EmptyResult () {
        // given
        when(apiInformationApi.getApis(eq("Bearer " + data.TOKEN), eq(data.COBA_ACTIVITY_ID), eq(null))).thenReturn(Mono.just(data.EMPTY));

        // when
        RuntimeException result = Assertions.assertThrows(RuntimeException.class, () -> uut.loadApiDetails(data.COBA_ACTIVITY_ID).collectList().block());

        // then
        assertThat(result.getMessage(), equalTo("No APIs could be loaded"));
    }


    @Test
    public void Test_loadApiDetails_DAOThrowsException () {
        // given
        when(apiInformationApi.getApis(eq("Bearer " + data.TOKEN), eq(data.COBA_ACTIVITY_ID), eq(null))).thenThrow(new RestClientException("Unable to load"));

        // when
        RestClientException result = Assertions.assertThrows(RestClientException.class, () -> uut.loadApiDetails(data.COBA_ACTIVITY_ID).collectList().block());

        // then
        assertThat(result.getMessage(), equalTo("Unable to load"));
    }


    @Test
    public void Test_loadApiDetails_BackendMissing_ReturnOk () {
        // given
        when(apiInformationApi.getApis(eq("Bearer " + data.TOKEN), eq(data.COBA_ACTIVITY_ID), eq(null))).thenReturn(Mono.just(data.ALL_MISSING_BACKEND));

        // when
        List<ApiInstance> result = uut.loadApiDetails(data.COBA_ACTIVITY_ID).collectList().block();

        // then
        assertThat(result.size(), equalTo(data.ALL_MISSING_BACKEND_LIST.size()));


        for (ApiInstance api : result) {
            assertThat(api.getApiId(), notNullValue());
            assertThat(api.getBaseUrl(), notNullValue());
            assertThat(api.getName(), notNullValue());
            assertThat(api.getBackendUrl(), nullValue());
        }
    }

    @Test
    public void Test_loadApiDetails_WithMissingBackends_AllOk () {
        // given
        when(apiInformationApi.getApis(eq("Bearer " + data.TOKEN), eq(data.COBA_ACTIVITY_ID), eq(null))).thenReturn(Mono.just(data.ALL));

        // when
        List<ApiInstance> result = uut.loadApiDetails(data.COBA_ACTIVITY_ID).collectList().block();

        // then
        assertThat(result.size(), equalTo(data.ALL_LIST.size()));

        for (ApiInstance api : result) {
            assertThat(api.getApiId(), notNullValue());
            assertThat(api.getBaseUrl(), notNullValue());
            assertThat(api.getName(), notNullValue());
        }
    }


    @Test
    public void Test_loadApiDetails_AllOk () {
        // given
        when(apiInformationApi.getApis(eq("Bearer " + data.TOKEN), eq(data.COBA_ACTIVITY_ID), eq(null))).thenReturn(Mono.just(data.ALL_OK));

        // when
        List<ApiInstance> result = uut.loadApiDetails(data.COBA_ACTIVITY_ID).collectList().block();

        // then
        assertThat(result.size(), equalTo(data.ALL_OK_LIST.size()));

        for (ApiInstance api : result) {
            assertThat(api.getApiId(), notNullValue());
            assertThat(api.getBackendUrl(), notNullValue());
            assertThat(api.getName(), notNullValue());
            assertThat(api.getBackendUrl(), notNullValue());
        }
    }
}
