package com.study.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NioClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        // 1. 创建 SocketChannel 并连接服务端
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("localhost", 8080));

        // 等待连接完成
        while (!socketChannel.finishConnect()) {
            // 可在此做其他事情（非阻塞特性）
        }
        System.out.println("已连接到服务器");

        Scanner scanner = new Scanner(System.in);
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (true) {
            System.out.print("请输入消息: ");
            String message = scanner.nextLine();
            if ("exit".equalsIgnoreCase(message)) break;

            // 2. 发送消息到服务端
            buffer.clear();
            buffer.put(message.getBytes());
            buffer.flip();
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }

            Thread.sleep(100);
            // 3. 接收服务端响应
            buffer.clear();
            int bytesRead = socketChannel.read(buffer);
            if (bytesRead > 0) {
                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                System.out.println("服务器响应: " + new String(data));
            }
        }
        socketChannel.close();
    }
}
