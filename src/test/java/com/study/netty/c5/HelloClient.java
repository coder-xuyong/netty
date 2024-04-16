package com.study.netty.c5;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Date;

/**
 * @author Xuyong
 */
public class HelloClient {
    public static void main(String[] args) {
        try {
            new Bootstrap()
                    .group(new NioEventLoopGroup()) // 1 创建 NioEventLoopGroup，同 Server
                    .channel(NioSocketChannel.class) // 2 选择客户 Socket 实现类，NioSocketChannel 表示基于 NIO 的客户端实现
                    .handler(new ChannelInitializer<Channel>() { // 3 添加 SocketChannel 的处理器，ChannelInitializer 处理器（仅执行一次），它的作用是待客户端 SocketChannel 建立连接后，执行 initChannel 以便添加更多的处理器
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new StringEncoder()); // 8 消息会经过通道 handler 处理，这里是将 String => ByteBuf 发出

                            //数据经过网络传输，到达服务器端，服务器端 5 和 6 处的 handler 先后被触发，走完一个流程

                        }
                    })
                    .connect("127.0.0.1", 8080) // 4 指定要连接的服务器和端口
                    .sync() // 5 Netty 中很多方法都是异步的，如 connect，这时需要使用 sync 方法等待 connect 建立连接完毕
                    .channel() // 6 获取 channel 对象，它即为通道抽象，可以进行数据读写操作
                    .writeAndFlush(new Date() + ": hello world!"); // 7 写入消息并清空缓冲区
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
