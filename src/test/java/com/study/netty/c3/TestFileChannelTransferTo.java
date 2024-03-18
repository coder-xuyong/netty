package com.study.netty.c3;

import com.study.netty.utils.CloseableFileInputStream;
import com.study.netty.utils.CloseableFileOutputStream;


import java.nio.channels.FileChannel;

/**
 * @author Xuyong
 */
public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        long start = System.nanoTime();
        try (
                CloseableFileInputStream fromChannel = new CloseableFileInputStream("data.txt");
                FileChannel from = fromChannel.getChannel();
                CloseableFileOutputStream toChannel = new CloseableFileOutputStream("to.txt");
                FileChannel to = toChannel.getChannel()
        ) {
            //效率高，底层会利用操作系统的零拷贝进行优化
            long size = from.size();
            //left 变量代表还剩多少字节
            for(long left = size; left > 0; ){
                System.out.println("position:" + (size - left) + " left:" + left);
                left -= from.transferTo(size - left, left, to);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        System.out.println("transferTo 用时：" + (end - start) / 1000_000.0 +" ms");
    }
}


