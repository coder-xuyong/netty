package com.study.netty.c5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Xuyong
 */
@Slf4j
public class WorkerServer {
    public static void main(String[] args) {
        try {
            new ServerBootstrap()
                    .group(new NioEventLoopGroup(1), new NioEventLoopGroup(2))
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                    ByteBuf byteBuf = msg instanceof ByteBuf ? ((ByteBuf) msg) : null;
                                    if (byteBuf != null) {
                                        byte[] buf = new byte[16];
                                        ByteBuf len = byteBuf.readBytes(buf, 0, byteBuf.readableBytes());
                                        log.debug(new String(buf));
                                    }
                                }
                            });
                        }
                    }).bind(8080).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
