package com.study.netty.c6.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.netty.c6.packet.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * @author xuyong
 * 结合了decode和encode的类，统一实现
 */
@Slf4j
public class ProtocolCodec extends MessageToMessageCodec<ByteBuf, BaseMessage> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext ctx, BaseMessage msg, List<Object> out) throws Exception {
        // 1. 序列化消息
        byte[] data = mapper.writeValueAsBytes(msg);
        ByteBuf buffer = ctx.alloc().buffer();
        // 2. 写入协议头（4字节长度 + 4字节类型），总长度 = 数据长度 + 类型字段4字节
        buffer.writeInt(data.length + 4);
        buffer.writeInt(msg.getType());
        buffer.writeBytes(data);
        out.add(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 1. 检查包头长度
        if (in.readableBytes() < 8) return; // 长度头4字节 + 类型4字节

        in.markReaderIndex();
        int totalLength = in.readInt();
        int type = in.readInt();

        // 2. 检查数据完整性
        if (in.readableBytes() < totalLength - 4) { // totalLength包含自身4字节
            in.resetReaderIndex();
            return;
        }

        // 3. 读取数据体
        byte[] data = new byte[totalLength - 4];
        in.readBytes(data);

        // 4. 反序列化
        Class<? extends BaseMessage> clazz = getMessageClass(type);
        if (clazz != null) {
            BaseMessage msg = mapper.readValue(data, clazz);
            out.add(msg);
        }
    }

    // 根据类型获取消息类
    private Class<? extends BaseMessage> getMessageClass(int type) {
        switch (type) {
            case 1:
                return HeartbeatRequest.class;
            case 2:
                return BusinessRequest.class;
            case 3:
                return BusinessResponse.class;
            case 4:
                return HeartbeatResponse.class;
            default:
                throw new IllegalArgumentException("未知消息类型: " + type);
        }
    }
}
