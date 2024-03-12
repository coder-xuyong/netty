package com.study.netty.c1;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static com.study.netty.c1.ByteBufferUtil.debugAll;

/**
 * @author Xuyong
 */
public class TestScatteringReads {
    public static void main(String[] args) {
        try (CloseableRandomAccessFile closeableFile = new CloseableRandomAccessFile("words.txt", "r");
             FileChannel channel = closeableFile.getChannel()) {
            ByteBuffer b1 = ByteBuffer.allocate(3);
            ByteBuffer b2 = ByteBuffer.allocate(3);
            ByteBuffer b3 = ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[]{b1, b2, b3});
            b1.flip();
            b2.flip();
            b3.flip();
            debugAll(b1);
            debugAll(b2);
            debugAll(b3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
