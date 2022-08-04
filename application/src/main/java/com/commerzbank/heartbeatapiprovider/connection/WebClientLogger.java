package com.commerzbank.heartbeatapiprovider.connection;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.StandardCharsets;

/**
 * This class logs complete request and responses. Chunked messages get one log per chunk.
 * To use this with a http client:
 * <pre>{@code
 * httpClient.doOnRequest((httpClientRequest, connection) -> connection.addHandlerFirst(new WebClientLogger()));
 * }</pre>
 * To use this with tcp clients:
 * <pre>{@code
 * tcpClient.bootstrap(b -> BootstrapHandlers.updateLogSupport(b, new WebClientLogger()));
 * }</pre>
 * Logging is done on trace level, so don't forget to set the log level.
 * <p></p>
 * This class is inspired by <a href="https://www.baeldung.com/spring-log-webclient-calls">Baeldung</a> and
 * <a href="https://stackoverflow.com/questions/59486719/formatting-wiretap-output-in-webflux">user2761431 @SO</a>
 */
public class WebClientLogger extends LoggingHandler {

    /**
     * creates a new instance with the logger name {@code com.commerzbank.xpay.service.connection.WebClientLogger}
     */
    public WebClientLogger() {
        this(WebClientLogger.class);
    }

    /**
     * creates a new instance with the specified logger name
     */
    public WebClientLogger(Class<?> clazz) {
        super(clazz, LogLevel.TRACE);
    }

    @Override
    protected String format(ChannelHandlerContext ctx, String event, Object arg) {
        if (arg instanceof ByteBuf) {
            return ((ByteBuf) arg).toString(StandardCharsets.UTF_8);
        } else if (arg instanceof ByteBufHolder) {
            return (((ByteBufHolder) arg).content()).toString(StandardCharsets.UTF_8);
        }
        return super.format(ctx, event, arg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.fireChannelActive();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.fireChannelReadComplete();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        ctx.fireChannelUnregistered();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ctx.fireChannelInactive();
    }

    @Override
    public void flush(ChannelHandlerContext ctx) {
        ctx.flush();
    }
}
