package com.study.netty.c5;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author Xuyong
 */
public class WorkerClient {
    public static void main(String[] args) {
        Channel channel = null;
        try {
            channel = new Bootstrap()
                    .group(new NioEventLoopGroup(1))
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            System.out.println("init...");
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        }
                    })
                    .channel(NioSocketChannel.class).connect("localhost", 8080)
                    .sync()
                    .channel();

            channel.writeAndFlush(ByteBufAllocator.DEFAULT.buffer().writeBytes("wangwu".getBytes()));
            Thread.sleep(2000);
            channel.writeAndFlush(ByteBufAllocator.DEFAULT.buffer().writeBytes("wangwu".getBytes()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
