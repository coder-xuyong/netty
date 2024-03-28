package com.study.netty.c4;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author Xuyong
 */
public class WriteServer {
    public static void main(String[] args) {
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            ssc.configureBlocking(false);


            Selector selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);

            ssc.bind(new InetSocketAddress(8080));

            while (true) {
                selector.select();
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    if (key.isAcceptable()) {
                        SocketChannel sc = ssc.accept();
                        sc.configureBlocking(false);
                        SelectionKey scKey = sc.register(selector, 0, null);

                        scKey.interestOps(SelectionKey.OP_READ);
                        //1.向客户端发送大量数据
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < 300000000; i++) {
                            sb.append("a");
                        }
                        ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                        //2.返回实际写入的字节数
                        int write = sc.write(buffer);
                        System.out.println(write);

                        //3.判断内容是否还有剩余内容
                        if (buffer.hasRemaining()) {
                            //4.关注可写事件              1                   4
                            scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
//                            scKey.interestOps(scKey.interestOps() | SelectionKey.OP_WRITE);
                            //5.把未写完的数据挂到 scKey 上
                            scKey.attach(buffer);
                        }
                    } else if (key.isWritable()) {
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        SocketChannel sc = (SocketChannel) key.channel();

                        int write = sc.write(buffer);
                        System.out.println(write);

                        //6.清理操作
                        if (!buffer.hasRemaining()){
                            //需要清楚buffer，新关联一个null，可以把上一次关注的取消
                            key.attach(null);
                            //不需要继续关注write事件
                            key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
