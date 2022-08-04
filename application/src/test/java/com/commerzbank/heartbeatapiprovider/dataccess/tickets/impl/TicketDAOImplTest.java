package com.commerzbank.heartbeatapiprovider.dataccess.tickets.impl;

import com.commerzbank.heartbeatapiprovider.configuration.ResourceServer;
import com.commerzbank.heartbeatapiprovider.dataccess.model.TicketDTO;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ressources.TestData;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TicketDAOImplTest {

    @Mock
    private ResourceServer resourceServer;

    @Mock
    private WebClient keycloakWebClient;

    private WebClient.RequestBodySpec bodyMock;
    private WebClient.ResponseSpec responseMock;


    private TicketDAOImpl uut;

    private TestData data;


    @BeforeEach
    void setUp () {
        data = new TestData();
        resourceServer = data.RESOURCE_SERVER;
        keycloakWebClient = mock(WebClient.class);

        WebClient.RequestBodyUriSpec postMock = mock(WebClient.RequestBodyUriSpec.class);
        bodyMock = mock(WebClient.RequestBodySpec.class);
        responseMock = mock(WebClient.ResponseSpec.class);


        when(keycloakWebClient.post()).thenReturn(postMock);
        when(postMock.uri(any(String.class))).thenReturn(bodyMock);
        when(bodyMock.body(any(BodyInserter.class))).thenReturn(bodyMock);
        when(responseMock.bodyToMono(TicketDTO.class)).thenReturn(Mono.just(data.TICKET_DTO));

        uut = new TicketDAOImpl(resourceServer, keycloakWebClient);
    }

    @Test
    void Test_loadJWT_Ok () {
        // given
        when(bodyMock.retrieve()).thenReturn(responseMock);

        // when
        String result = uut.loadJWT().block();

        // then
        assertThat(result, equalTo(data.TICKET_DTO.getAccess_token()));
    }

    @Test
    void Test_loadJWT_SaveToken () {
        // given
        when(bodyMock.retrieve()).thenReturn(responseMock);

        // when
        String result1 = uut.loadJWT().block();
        String resul2 = uut.loadJWT().block();

        // then
        assertThat(result1, equalTo(resul2));
        verify(bodyMock, times(1)).retrieve();
    }

    @Test
    @Order(1) // This has to run first, because of the static variable in TicketDAO
    void Test_loadJWT_ServerNotResponding_ThrowError () {
        // given
        when(bodyMock.retrieve()).thenThrow(new RuntimeException("Some Error"));

        // when
        RuntimeException result = Assertions.assertThrows(RuntimeException.class, () -> uut.loadJWT().block());

        // then
        assertThat(result.getMessage(), equalTo("Some Error"));
        verify(bodyMock, times(1)).retrieve();
    }
}
