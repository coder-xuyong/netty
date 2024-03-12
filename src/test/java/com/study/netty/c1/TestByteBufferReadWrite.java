package com.study.netty.c1;

import java.nio.ByteBuffer;

import static com.study.netty.c1.ByteBufferUtil.debugAll;

/**
 * @author Xuyong
 */
public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);// 'a'
        buffer.put(new byte[]{0x62,0x63,0x64});// b c d
        debugAll(buffer);
//        System.out.println(buffer.get());
        buffer.flip();//切换为读模式
        System.out.println(buffer.get());
        debugAll(buffer);

        buffer.compact();//向前压缩，切换到写模式
        debugAll(buffer);
        buffer.put(new byte[]{0x65,0x6f});
        debugAll(buffer);
    }
}
