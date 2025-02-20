package com.study.netty;

import com.study.netty.handler.ClientMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author xuyong
 */
@Slf4j
public class NettyClient {

    /**
     * 最大重连间隔
     */
    private final static int MAX_RETRY = 3;
    private final EventLoopGroup clientGroup = new NioEventLoopGroup();
    protected Channel channel = null;
    Bootstrap bootstrap = new Bootstrap();

    public NettyClient() {
        bootstrap
                .group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                        // 取代 StringEncoder，方便自定义内容
                        ch.pipeline().addLast(new ClientMessageHandler());
                    }
                });
    }

    public static void main(String[] args) throws InterruptedException {
        String serverIp = "127.0.0.1";
        int serverPort = 1314;
        NettyClient nettyClient = new NettyClient();
        nettyClient.start(serverIp, serverPort, NettyClient.MAX_RETRY);
    }

    public void start(String host, int port, int retry) throws InterruptedException {
        // 此处的 connect 是异步
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                channel = ((ChannelFuture) future).channel();
                log.info("连接成功,开始执行任务。");
                startTask(channel);
            } else if (retry == 0) {
                log.error("重试次数已用完，放弃连接！");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 定时任务下次执行重连的时间
                int delay = 1 << order;
                log.error(": 连接失败，第" + order + "次重连……");

                bootstrap.config().group().schedule(() -> {
                    try {
                        start(host, port, retry - 1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, delay, TimeUnit.SECONDS);
            }
        });
    }

    public boolean isActive() {
        if (channel == null) {
            return false;
        }
        return channel.isActive() && channel.isOpen() && channel.isRegistered();
    }

    private void startTask(@NonNull Channel channel) {
        channel.writeAndFlush("hello world");
        clientGroup.shutdownGracefully();
//        new Thread(() -> {
//            while (!Thread.interrupted() && isActive()) {
//                channel.writeAndFlush("hello world");
//                log.info("send msg");
//            }
//            if (!isActive()) {
//                log.warn("连接已断开，请重启客户端或者等待重连任务");
//            }
//        }).start();
    }
}
