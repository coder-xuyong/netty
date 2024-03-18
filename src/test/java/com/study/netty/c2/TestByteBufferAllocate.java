package com.study.netty.c2;

import java.nio.ByteBuffer;

/**
 * @author Xuyong
 */
public class TestByteBufferAllocate {
    public static void main(String[] args) {
        // class java.nio.HeapByteBuffer   -java 堆内存 读写效率较低，受到gc 的影响
        System.out.println(ByteBuffer.allocate(16).getClass());
        // class java.nio.DirectByteBuffer    - 直接内存 读写效率高（少一次拷贝），不会受到 gc 影响
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
    }
}
