package com.study.netty.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Xuyong
 */
public class WriteClient {
    public static void main(String[] args) {
        try (SocketChannel sc = SocketChannel.open()){
            sc.connect(new InetSocketAddress("localhost",8080));
            //3.接受数据
            int count = 0;
            while (true){
                ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                count += sc.read(buffer);
                System.out.println(count);
                buffer.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
