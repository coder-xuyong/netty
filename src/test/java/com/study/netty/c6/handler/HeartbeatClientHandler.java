package com.study.netty.c6.handler;

import com.study.netty.c6.packet.HeartbeatRequest;
import com.study.netty.c6.packet.HeartbeatResponse;
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
public class HeartbeatClientHandler extends ChannelInboundHandlerAdapter {
    private static final String CLIENT_PING = "CLIENT_PING";
    private static final String SERVER_PING = "SERVER_PING";

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {

            IdleStateEvent event = (IdleStateEvent) evt;
//            if (event.state() == IdleState.WRITER_IDLE) {
//                // 发送心跳包
//                ctx.writeAndFlush(CLIENT_PING);
//                log.info("[CLIENT] 发送心跳: " + new Date());
////                // 写空闲时发送心跳包
//                ctx.writeAndFlush(Unpooled.wrappedBuffer("PING".getBytes()));
//                System.out.println("发送心跳包");
//            }
            if (event.state() == IdleState.WRITER_IDLE) {
                HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
                // 发送心跳包
                ctx.writeAndFlush(heartbeatRequest);
                log.info("[CLIENT] 发送心跳，reqId:{}",heartbeatRequest.getReqId());
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        if (SERVER_PING.equals(msg)) {
//            log.info("[CLIENT] 收到服务端探测包");
//        }
        if (msg instanceof HeartbeatResponse){
            log.info("[SERVER] 回复心跳，reqId:{}",((HeartbeatResponse) msg).getReqId());
            ReferenceCountUtil.release(msg);
        }else {
            // 非心跳消息传递给后续 Handler（如 ServerBusinessHandler）
            ctx.fireChannelRead(msg);
        }
    }

//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) {
//        log.error("连接断开，触发重连...");
//        // 触发重连逻辑（实际中应通过 Client 类管理）
//        ctx.channel().eventLoop().execute(() -> {
//            // 此处应通过 Client 实例调用重连方法，示例简化处理
//            // 实际需确保线程安全和资源管理
//            String serverIp = "127.0.0.1";
//            int serverPort = 1314;
//            try {
//                new NettyClient().start(serverIp, serverPort, NettyClient.MAX_RETRY);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                log.error("重连失败");
//            }
//        });
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
