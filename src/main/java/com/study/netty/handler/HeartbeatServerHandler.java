package com.study.netty.handler;

import com.study.netty.packet.HeartbeatRequest;
import com.study.netty.packet.HeartbeatResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xuyong
 */
@Slf4j
public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    log.info("读空闲超时，关闭连接,[SERVER.CHANNEL.ID：{}]无心跳响应" ,ctx.channel().id());
                    ctx.close();
                    break;
                case WRITER_IDLE:
                    log.info("写空闲超时，发送心跳");
                    // 发送心跳包
                    ctx.writeAndFlush("SERVER_PING");
                    break;
                case ALL_IDLE:
                    log.info("读写均空闲");
                    // 主动发送服务端心跳
                    ctx.writeAndFlush("SERVER_PING");
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HeartbeatRequest) {
            log.info("[SERVER] 收到心跳，reqId:{}", ((HeartbeatRequest) msg).getReqId());
            HeartbeatResponse response = new HeartbeatResponse(((HeartbeatRequest) msg).getReqId());
            response.setType(4);
            ctx.writeAndFlush(response);
            // 心跳消息无需传递到业务处理器
            ReferenceCountUtil.release(msg);
        }else {
            // 非心跳消息传递给后续 Handler（如 ServerBusinessHandler）
            ctx.fireChannelRead(msg);
        }
    }
}
