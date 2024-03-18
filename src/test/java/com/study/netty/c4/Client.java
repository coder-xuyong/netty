package com.study.netty.c4;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080)) {
            System.out.println(socket);
            socket.getOutputStream().write("world".getBytes());
            int read = System.in.read();
            System.out.println(read);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void m1(String[] args) {
        try (SocketChannel sc = SocketChannel.open()) {
            sc.connect(new InetSocketAddress("localhost", 8080));
            System.out.println("waiting...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
