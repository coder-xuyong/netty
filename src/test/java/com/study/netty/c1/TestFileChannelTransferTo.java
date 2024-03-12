package com.study.netty.c1;

import com.study.netty.utils.CloseableFileInputStream;
import com.study.netty.utils.CloseableFileOutputStream;


import java.nio.channels.FileChannel;

/**
 * @author Xuyong
 */
public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        try (
                CloseableFileInputStream fromChannel = new CloseableFileInputStream("D:\\BaiduNetdiskDownload\\黑马程序员\\黑马springcloud.zip");
                FileChannel from = fromChannel.getChannel();
                CloseableFileOutputStream toChannel = new CloseableFileOutputStream("to.zip");
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
    }
}


