package com.study.netty.c6.handler;

import com.study.netty.c6.packet.BaseMessage;
import com.study.netty.c6.packet.BusinessRequest;
import com.study.netty.c6.packet.BusinessResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xuyong
 */
@Slf4j
public class ClientBusinessHandler extends SimpleChannelInboundHandler<BaseMessage> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端 发送消息 你好");
        ctx.writeAndFlush(new BusinessRequest("你好", 1));
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMessage msg) {
        if (msg instanceof BusinessResponse) {
            BusinessResponse resp = (BusinessResponse) msg;
            log.info("[客户端] 收到响应: " + resp.getResult());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("远程主机强迫关闭了一个现有的连接。");
        super.exceptionCaught(ctx, cause);
    }
}
