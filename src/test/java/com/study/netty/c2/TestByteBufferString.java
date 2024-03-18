package com.study.netty.c2;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.study.netty.c2.ByteBufferUtil.debugAll;

/**
 * @author Xuyong
 */
public class TestByteBufferString {
    public static void main(String[] args) {
        // 1. 字符串转为 ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put("hello".getBytes());
        debugAll(buffer);

        // 2. Charset
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer2);

        // 3. wrap
        ByteBuffer buffer3 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer3);

        String str1 = StandardCharsets.UTF_8.decode(buffer3).toString();
        System.out.println("str1:" + str1);

        buffer.flip();//转换为读模式
        String str2 = StandardCharsets.UTF_8.decode(buffer).toString();
        System.out.println("str2:" + str2);

    }
}
