package com.study.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NioServer {
    public static void main(String[] args) throws IOException {
        // 1. 创建 ServerSocketChannel 并绑定端口
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false); // 非阻塞模式

        // 2. 创建 Selector 并注册 ACCEPT 事件
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("NIO 服务器启动，监听端口 8080...");

        while (true) {
            selector.select(); // 阻塞直到有事件就绪
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iter = keys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove(); // 必须移除已处理的 key

                if (key.isAcceptable()) {
                    // 3. 处理新连接
                    ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                    SocketChannel clientChannel = serverChannel.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("客户端连接: " + clientChannel.getRemoteAddress());

                } else if (key.isReadable()) {
                    // 4. 处理读事件
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int bytesRead = clientChannel.read(buffer);

                    if (bytesRead == -1) { // 连接关闭
                        key.cancel();
                        clientChannel.close();
                        continue;
                    }

                    buffer.flip();
                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);
                    String message = new String(data);
                    System.out.println("收到消息: " + message);

                    // 5. 注册写事件，准备回传数据
                    clientChannel.register(selector, SelectionKey.OP_WRITE, message);

                } else if (key.isWritable()) {
                    // 6. 处理写事件
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    String response = (String) key.attachment();
                    ByteBuffer buffer = ByteBuffer.wrap(("ECHO: " + response).getBytes());
                    clientChannel.write(buffer);

                    // 重置为读事件，等待下次请求
                    clientChannel.register(selector, SelectionKey.OP_READ);
                }
            }
        }
    }
}