package com.study.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Date;

/**
 * @author xuyong
 */
public class NettyClient {

    public static void main(String[] args) {
        new NettyClient();
    }
    public NettyClient() {
        try {
            new Bootstrap()
                    .group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new StringEncoder());
                        }
                    })
                    .connect("127.0.0.1", 8080)
                    .sync()
                    .channel()
                    .writeAndFlush(new Date() + ": hello world!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
