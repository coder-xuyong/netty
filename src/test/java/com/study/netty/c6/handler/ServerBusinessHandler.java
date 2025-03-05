package com.study.netty.c6.handler;

import com.study.netty.c6.enums.MsgTypeEnum;
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
public class ServerBusinessHandler extends SimpleChannelInboundHandler<BaseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMessage msg) throws Exception {
        if (msg instanceof BusinessRequest) {
            BusinessRequest req = (BusinessRequest) msg;
            if (((BusinessRequest) msg).getMsgType() == MsgTypeEnum.CONFIG.getCode()) {
                BusinessResponse resp = new BusinessResponse(req.getReqId(), true, "配置处理成功");
                ctx.writeAndFlush(resp);
            } else if (((BusinessRequest) msg).getMsgType() == MsgTypeEnum.DATA.getCode()) {
                BusinessResponse resp = new BusinessResponse(req.getReqId(), true, "数据处理成功");
                ctx.writeAndFlush(resp);
            }
        }
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel 连接成功");
        super.channelActive(ctx);
    }
}
