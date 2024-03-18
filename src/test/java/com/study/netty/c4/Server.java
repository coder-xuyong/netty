package com.study.netty.c4;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.study.netty.c2.ByteBufferUtil.debugAll;
import static com.study.netty.c2.ByteBufferUtil.debugRead;

@Slf4j
public class Server {


    public static void main(String[] args) {
        // 1. 创建 selector, 管理多个 channel
        try (Selector selector = Selector.open();
             ServerSocketChannel ssc = ServerSocketChannel.open()) {
            ssc.configureBlocking(false);
            // 2. 建立 selector 和 channel 的联系（注册）
            // SelectionKey 就是将来事件发生后，通过它可以知道事件和哪个channel的事件
            SelectionKey sscKey = ssc.register(selector, 0, null);
            // key 只关注 accept 事件
            sscKey.interestOps(SelectionKey.OP_ACCEPT);
            log.debug("sscKey:{}", sscKey);
            ssc.bind(new InetSocketAddress(8080));
            while (true) {
                // 3. select 方法, 没有事件发生，线程阻塞，有事件，线程才会恢复运行
                // select 在事件未处理时，它不会阻塞, 事件发生后要么处理，要么取消，不能置之不理
                selector.select();
                // 4. 处理事件, selectedKeys 内部包含了所有发生的事件
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator(); // accept, read
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    // 处理key 时，要从 selectedKeys 集合中删除，否则下次处理就会有问题
                    iter.remove();
                    log.debug("key: {}", key);
                    // 5. 区分事件类型
                    if (key.isAcceptable()) { // 如果是 accept
                        try(ServerSocketChannel channel = (ServerSocketChannel) key.channel()){
                            SocketChannel sc = channel.accept();
                            sc.configureBlocking(false);
                            ByteBuffer buffer = ByteBuffer.allocate(16); // attachment
                            // 将一个 byteBuffer 作为附件关联到 selectionKey 上
                            SelectionKey scKey = sc.register(selector, 0, buffer);
                            scKey.interestOps(SelectionKey.OP_READ);
                            log.debug("{}", sc);
                            log.debug("scKey:{}", scKey);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else if (key.isReadable()) { // 如果是 read
                        // 拿到触发事件的channel
                        try (SocketChannel channel = (SocketChannel) key.channel()){
                            // 获取 selectionKey 上关联的附件
                            ByteBuffer buffer = (ByteBuffer) key.attachment();
                            int read = channel.read(buffer); // 如果是正常断开，read 的方法的返回值是 -1
                            if (read == -1) {
                                key.cancel();
                            } else {
                                split(buffer);
                                // 需要扩容
                                if (buffer.position() == buffer.limit()) {
                                    ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                    buffer.flip();
                                    newBuffer.put(buffer); // 0123456789abcdef3333\n
                                    key.attach(newBuffer);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            key.cancel();  // 因为客户端断开了,因此需要将 key 取消（从 selector 的 keys 集合中真正删除 key）
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void m3(String[] args) {
        try (ServerSocketChannel channel = ServerSocketChannel.open()) {
            channel.bind(new InetSocketAddress(8080));
            System.out.println(channel);
            Selector selector = Selector.open();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                int count = selector.select();
                log.debug("select count: {}", count);
                // 获取所有事件
                Set<SelectionKey> keys = selector.selectedKeys();

                // 遍历所有事件，逐一处理
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    // 判断事件类型
                    if (key.isAcceptable()) {
                        try (ServerSocketChannel c = (ServerSocketChannel) key.channel()) {
                            // 必须处理
                            SocketChannel sc = c.accept();
                            sc.configureBlocking(false);
                            sc.register(selector, SelectionKey.OP_READ);
                            log.debug("连接已建立: {}", sc);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(128);
                        int read = sc.read(buffer);
                        if (read == -1) {
                            key.cancel();
                            sc.close();
                        } else {
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                    // 处理完毕，必须将事件移除
                    iter.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void m2(String[] args) {
        // 使用 nio 来理解非阻塞模式, 单线程
        // 0. ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 1. 创建了服务器
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {

            ssc.configureBlocking(false); // 非阻塞模式
            // 2. 绑定监听端口
            ssc.bind(new InetSocketAddress(8080));
            // 3. 连接集合
            List<SocketChannel> channels = new ArrayList<>();
            while (true) {
                // 4. accept 建立与客户端连接， SocketChannel 用来与客户端之间通信
                SocketChannel sc = ssc.accept(); // 非阻塞，线程还会继续运行，如果没有连接建立，但sc是null
                if (sc != null) {
                    log.debug("connected... {}", sc);
                    sc.configureBlocking(false); // 非阻塞模式
                    channels.add(sc);
                }
                for (SocketChannel channel : channels) {
                    // 5. 接收客户端发送的数据
                    int read = channel.read(buffer);// 非阻塞，线程仍然会继续运行，如果没有读到数据，read 返回 0
                    if (read > 0) {
                        buffer.flip();
                        debugRead(buffer);
                        buffer.clear();
                        log.debug("after read...{}", channel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void m1(String[] args) {

        // 使用 nio 来理解阻塞模式, 单线程
        // 0. ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 1. 创建了服务器
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            // 2. 绑定监听端口
            ssc.bind(new InetSocketAddress(8080));

            // 3. 连接集合
            List<SocketChannel> channels = new ArrayList<>();
            while (true) {
                // 4. accept 建立与客户端连接， SocketChannel 用来与客户端之间通信
                log.debug("connecting...");
                SocketChannel sc = ssc.accept(); // 阻塞方法，线程停止运行
                log.debug("connected... {}", sc);
                channels.add(sc);
                for (SocketChannel channel : channels) {
                    // 5. 接收客户端发送的数据
                    log.debug("before read... {}", channel);
                    // 阻塞方法，线程停止运行
                    channel.read(buffer);
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.debug("after read...{}", channel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整消息
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                // 把这条完整消息存入新的 ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从 source 读，向 target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact(); // 0123456789abcdef  position 16 limit 16
    }
}
