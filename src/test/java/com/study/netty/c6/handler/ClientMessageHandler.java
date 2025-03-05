package com.study.netty.c6.handler;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author xuyong
 * 和 StringEncoder 一样继承 MessageToMessageEncoder，这里和  StringEncoder 一样。
 */
public class ClientMessageHandler extends MessageToMessageEncoder<CharSequence> {

    private final Charset charset;

    public ClientMessageHandler() {
        this(Charset.defaultCharset());
    }

    public ClientMessageHandler(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        } else {
            this.charset = charset;
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
        if (msg.length() != 0) {
            out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg), this.charset));
        }
    }
}
