package com.study.netty.c6;

import com.study.netty.c6.handler.ClientBusinessHandler;
import com.study.netty.c6.handler.HeartbeatClientHandler;
import com.study.netty.c6.handler.ProtocolCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
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
    public final static int MAX_RETRY = 5;
    private final EventLoopGroup clientGroup = new NioEventLoopGroup();
    private Channel channel = null;
    private Bootstrap bootstrap = new Bootstrap();

    public NettyClient() {
        bootstrap
                .group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
//                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                        // 取代 StringEncoder，方便自定义内容
//                        ch.pipeline().addLast(new ClientMessageHandler());
                        // 处理粘包/拆包（最大长度10MB）
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(10 * 1024 * 1024, 0, 4, 0, 0));
                        ch.pipeline().addLast(new ProtocolCodec());
                        // 当一定周期内(默认50s)没有收到对方任何消息时，需要主动关闭链接
                        ch.pipeline().addLast(new IdleStateHandler(0, 10, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new HeartbeatClientHandler());
                        ch.pipeline().addLast(new ClientBusinessHandler());
                    }
                });
    }

    public static void main(String[] args) throws InterruptedException {
        String serverIp = "127.0.0.1";
        int serverPort = 1314;
        new NettyClient().start(serverIp, serverPort, NettyClient.MAX_RETRY);
    }

    public void start(String host, int port, int retry) throws InterruptedException {
        // 此处的 connect 是异步
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                channel = ((ChannelFuture) future).channel();
                log.info("连接成功");
//                startTask(channel);
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
        channelFuture.sync();
    }

    public boolean isActive() {
        if (channel == null) {
            return false;
        }
        return channel.isActive() && channel.isOpen() && channel.isRegistered();
    }

    private void startTask(@NonNull Channel channel) {
        channel.writeAndFlush("hello world");
//        clientGroup.shutdownGracefully();
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
