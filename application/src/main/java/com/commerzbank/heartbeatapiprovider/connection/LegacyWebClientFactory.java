package com.commerzbank.heartbeatapiprovider.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.util.NettySslUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.TcpClient;
import reactor.netty.transport.ProxyProvider;

import javax.annotation.Nullable;
import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Primary
@Component
@Slf4j
@RequiredArgsConstructor
public class LegacyWebClientFactory {
    @Value("${spring.codec.max-in-memory-size}")
    private String bufferMaxInMemorySizeRAW;
    private static final double PERCENTAGE_RESPONSE_SIZE_LIMIT_TO_MAX_DATABUFFER_SIZE = 0.9F;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper mapper;
    private final SSLFactory sslFactory;

    /**
     * Creates a reactive {@link WebClient} with preconfigured {@link ObjectMapper}, {@link SslContext} and timeout settings,
     * using a TcpClient with a default connection pool and without a Proxy.
     * The underlying TcpClient may throw these exceptions for timeouts:
     * {@link ReadTimeoutException}, {@link WriteTimeoutException}, {@link ConnectTimeoutException}
     *
     * @param timeoutInMilliseconds the timeout for outgoing requests. Will be applied to the connection, read and write.
     *                              Idle remains at default settings.
     * @return A {@link WebClient} ready to be used.
     */
    public WebClient createWebClient(Integer timeoutInMilliseconds) {
        return createWebClient(timeoutInMilliseconds, null, null, null, null, null, null);
    }

    public WebClient createWebClient(Duration timeout) {
        return createWebClient((int) timeout.toMillis());
    }


    /**
     * Creates a reactive {@link WebClient} with preconfigured {@link ObjectMapper}, {@link SslContext} and timeout settings,
     * without using a Proxy.
     * The underlying TcpClient may throw these exceptions for timeouts:
     * {@link ReadTimeoutException}, {@link WriteTimeoutException}, {@link ConnectTimeoutException}
     *
     * @param timeoutInMilliseconds the timeout for outgoing requests. Will be applied to the connection, read and write.
     *                              Idle remains at default settings.
     * @param maxConnections        the number of max connections, null means default
     * @return A {@link WebClient} ready to be used.
     */
    public WebClient createWebClient(Integer timeoutInMilliseconds, @Nullable Integer maxConnections, @Nullable Integer pendingAcquireMaxCount) {
        return createWebClient(timeoutInMilliseconds, maxConnections, pendingAcquireMaxCount, null, null, null, null);
    }

    public WebClient createWebClient(Duration timeout, @Nullable Integer maxConnections, @Nullable Integer pendingAcquireMaxCount) {
        return createWebClient((int) timeout.toMillis(), maxConnections, pendingAcquireMaxCount);
    }

    public WebClient createWebClient(Duration timeout, @Nullable Integer maxConnections, @Nullable Integer pendingAcquireMaxCount, @Nullable String host,
                                     @Nullable Integer port, @Nullable String username, @Nullable String password) {
        return createWebClient((int) timeout.toMillis(), maxConnections, pendingAcquireMaxCount, host, port, username, password);
    }

    /**
     * Creates a reactive {@link WebClient} with preconfigured {@link ObjectMapper}, {@link SslContext} and timeout settings,
     * and configures a Proxy for the connection.
     * The underlying TcpClient may throw these exceptions for timeouts:
     *
     * @param timeoutInMilliseconds   the timeout for outgoing requests. Will be applied to the connection, read and write.
     *                                Idle remains at default settings.
     * @param maxConnections          the number of max connections, null means default
     * @param pendingAcquireMaxCount  max. number of requiered connections.
     * @param host                    the IP of the Host which is used for the proxy
     * @param port                    the port of the proxy connection
     * @param username                the username which is used for the proxy connection
     * @param password                the corresponding password to the used username
     * @return A {@link WebClient} ready to be used.
     */
    public WebClient createWebClient(Integer timeoutInMilliseconds, @Nullable Integer maxConnections, @Nullable Integer pendingAcquireMaxCount, @Nullable String host,
                                     @Nullable Integer port, @Nullable String username, @Nullable String password) {
        ExchangeStrategies strategies = ExchangeStrategies
                .builder()
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper, MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper, MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer.defaultCodecs().maxInMemorySize(500 * 1024 * 1024);
                }).build();

        // cb2ruq3: Apparently there are multiple Timeouts we can configure, and I have no idea which one to use.
        //  So I'll just use most of them. Except these:
        //  The request Timeout, also known as socket option SO_TIMEOUT, seems to be intended for blocking IO and thus is not used here.
        //  The IdleTimeoutHandler. I don't think we want to kill idle connections earlier than default.

        // cb2s1qz: Adding an option for limiting connection count. I actually don't have any clue if there are any caveats.

        TcpClient timeoutClient;

        if (maxConnections == null) {
            timeoutClient = TcpClient.create();
        } else {
            ConnectionProvider.Builder connectionProviderBuilder = ConnectionProvider.builder("fixedConnectionProvider")
                    .maxConnections(maxConnections)
                    .pendingAcquireTimeout(Duration.ofMillis(ConnectionProvider.DEFAULT_POOL_ACQUIRE_TIMEOUT));

            if (pendingAcquireMaxCount != null) {
                connectionProviderBuilder.pendingAcquireMaxCount(pendingAcquireMaxCount);
            }

            timeoutClient = TcpClient.create(connectionProviderBuilder.build());
        }

        timeoutClient = timeoutClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutInMilliseconds)
                .doOnConnected(
                        c -> c.addHandlerLast(new ReadTimeoutHandler(timeoutInMilliseconds, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(timeoutInMilliseconds, TimeUnit.MILLISECONDS))
                );

        HttpClient httpClient;

        if (host != null && port != null && username != null && password != null) {
            httpClient = buildHttpClientWithProxy(timeoutClient, host, port, username, password);
        } else {
            httpClient = HttpClient.from(timeoutClient)
                    .secure(t -> t.sslContext(createSslContext()));
        }

        // log connection errors (e.g. timeouts) in a clean way with CobaActivityId. Note: query params are not logged.
        httpClient = httpClient.doOnError(
                (req, t) -> log.error("Observed an connection error while sending on {} {} ", req.method(),
                        UriComponentsBuilder.fromUriString(req.uri()).replaceQuery(null).build(Collections.emptyMap()), t),
                (res, t) -> log.error("Observed an connection error while receiving on {} {} ", res.method(),
                        UriComponentsBuilder.fromUriString(res.uri()).replaceQuery(null).build(Collections.emptyMap()), t));

        WebClient.Builder webClient;
        if (LegacyWebClientFactory.log.isTraceEnabled()) {
            webClient = buildWebClientWithLogging(httpClient, strategies);
        } else {
            webClient = webClientBuilder
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .exchangeStrategies(strategies);
        }

        DataSize bufferMaxInMemorySize = DataSize.parse(bufferMaxInMemorySizeRAW);

        // A negative data-size indicates that there is no limit on the databuffer-size.
        // Set only a limit to the max remote response size, if the databuffer is limited itself.
        if (!bufferMaxInMemorySize.isNegative()) {
            long maxResponseSize = (long) (PERCENTAGE_RESPONSE_SIZE_LIMIT_TO_MAX_DATABUFFER_SIZE * bufferMaxInMemorySize.toBytes());

            webClient.filter(ExchangeFilterFunctions.limitResponseSize(maxResponseSize));
        }

        return webClient.build();
    }


    private SslContext createSslContext() {
        try {
            return NettySslUtils.forClient(sslFactory).build();
        } catch (SSLException e) {
            throw new IllegalArgumentException("Couldn't create ssl context.", e);
        }
    }

    HttpClient buildHttpClientWithProxy(TcpClient timeoutClient, String host, Integer port, String username, String password) {
        return HttpClient.from(timeoutClient)
                .tcpConfiguration(tcpClient -> tcpClient.proxy(ops -> ops.type(ProxyProvider.Proxy.HTTP)
                        .host(host)
                        .port(port)
                        .username(username)
                        .password(user -> password)
                )).secure(t -> t.sslContext(createSslContext()));
    }

    WebClient.Builder buildWebClientWithLogging(HttpClient httpClient, ExchangeStrategies strategies) {

        if (LoggerFactory.getLogger(HttpClient.class).isTraceEnabled()) {
            httpClient = httpClient.doOnRequest((httpClientRequest, connection) -> connection.addHandlerFirst(new WebClientLogger(HttpClient.class)));
            return webClientBuilder
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .exchangeStrategies(strategies);
        } else {
            Consumer<ClientCodecConfigurer> consumer = configurer ->
                    configurer.defaultCodecs().enableLoggingRequestDetails(true);
            return webClientBuilder
                    .codecs(consumer)
                    .filters(exchangeFilterFunctions -> {
                        exchangeFilterFunctions.add(logRequest());
                        exchangeFilterFunctions.add(logResponse());
                    })
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .exchangeStrategies(strategies);
        }
    }

    ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (LegacyWebClientFactory.log.isTraceEnabled()) {
                StringBuilder sb = new StringBuilder()
                        .append(clientRequest.method())
                        .append(" ")
                        .append(clientRequest.url())
                        .append("\n");

                clientRequest
                        .headers()
                        .forEach((name, values) -> sb.append(name).append(": ").append(String.join(", ", values)).append("\n"));

                LegacyWebClientFactory.log.trace("Request:");
                LegacyWebClientFactory.log.trace(sb.toString());
            }
            return Mono.just(clientRequest);
        });
    }

    ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (LegacyWebClientFactory.log.isTraceEnabled()) {
                LegacyWebClientFactory.log.trace("Response:");
                LegacyWebClientFactory.log.trace("  - headers:");
                clientResponse
                        .headers()
                        .asHttpHeaders()
                        .forEach((name, values) -> LegacyWebClientFactory.log.trace("    - {}: {}", name, values));
            }
            return Mono.just(clientResponse);
        });
    }
}
