package com.study.netty.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author Xuyong
 */
public class WriteClient {
    public static void main(String[] args) {
        try (SocketChannel sc = SocketChannel.open()) {
            sc.connect(new InetSocketAddress("localhost", 8080));
//            while (true){
//                sc.write(StandardCharsets.UTF_8.encode("hello"));
//                Thread.sleep(3000);
//            }
            //3.接受数据
            int count = 0;
            while (true) {
                ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                count += sc.read(buffer);
                System.out.println(count);
                buffer.clear();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
