package com.study.netty.c6.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author xuyong
 * 和 StringDecoder 一样继承 MessageToMessageDecoder，这里和  StringDEcoder 一样。
 */
@Slf4j
public class ServerMessageHandler extends MessageToMessageDecoder<ByteBuf> {
    private final Charset charset;

    public ServerMessageHandler() {
        this(Charset.defaultCharset());
    }

    public ServerMessageHandler(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        } else {
            this.charset = charset;
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        log.info("server 收到了 {}",msg.toString(this.charset));
        out.add(msg.toString(this.charset));
    }
}
