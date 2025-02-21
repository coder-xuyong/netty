package com.study.netty;

import com.study.netty.handler.HeartbeatServerHandler;
import com.study.netty.handler.ServerMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author xuyong
 */
@Slf4j
public class NettyServer {

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(2);
    public NettyServer() {
        serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        // netty 自带日志的 handler
//                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                        ch.pipeline().addLast(new ServerMessageHandler());
//                        ch.pipeline().addLast(new LifeCycleHandler());
                        ch.pipeline().addLast(new IdleStateHandler(15,0,20, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new HeartbeatServerHandler());
                    }
                });
    }

    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(1314);
    }

    public void start(int port) {
        ChannelFuture channelFuture = serverBootstrap.bind(port);
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                log.info("端口[{}]绑定成功", port);
            } else {
                log.error("端口[{}]绑定异常!", port);
            }
        });
        try {
            // 阻塞在此处，直到绑定端口完成
            channelFuture.sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
