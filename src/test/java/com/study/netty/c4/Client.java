package com.study.netty.c4;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void m2(String[] args) {
        try (Socket socket = new Socket("localhost", 8080)) {
            System.out.println(socket);
            socket.getOutputStream().write("world".getBytes());
            int read = System.in.read();
            System.out.println(read);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try (SocketChannel sc = SocketChannel.open()) {
            sc.connect(new InetSocketAddress("localhost", 8080));
            System.out.println("waiting...");
            sc.write(Charset.defaultCharset().encode("0123\n456789abcdef\n"));
//            sc.write(Charset.defaultCharset().encode("012345\n6789abcdef333333\n"));
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
