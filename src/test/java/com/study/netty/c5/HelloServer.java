package com.study.netty.c5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author Xuyong
 */
public class HelloServer {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup()) // 1 创建 NioEventLoopGroup，可以简单理解为 `线程池 + Selector` 后面会详细展开
                .channel(NioServerSocketChannel.class) // 2 选择服务 Socket 实现类，其中 NioServerSocketChannel 表示基于 NIO 的服务器端实现
                .childHandler(new ChannelInitializer<NioSocketChannel>() { // 3 为啥方法叫 childHandler，是接下来添加的处理器都是给 SocketChannel 用的，而不是给 ServerSocketChannel。ChannelInitializer 处理器（仅执行一次），它的作用是待客户端 SocketChannel 建立连接后，执行 initChannel 以便添加更多的处理器
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new StringDecoder()); // 5 SocketChannel 的处理器，解码 ByteBuf => String
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() { // 6 ，SocketChannel 的业务处理器，使用上一个处理器的处理结果
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                System.out.println(msg);
                            }
                        });
                    }
                })
                .bind(8080); // 4 ServerSocketChannel 绑定的监听端口
    }
}
