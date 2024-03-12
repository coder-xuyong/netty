package com.study.netty.c1;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * @author Xuyong
 */
public class CloseableRandomAccessFile implements Closeable {
    private final RandomAccessFile file;

    public CloseableRandomAccessFile(String fileName, String mode) throws IOException {
        this.file = new RandomAccessFile(fileName, mode);
    }

    public FileChannel getChannel() {
        return file.getChannel();
    }

    @Override
    public void close() throws IOException {
        file.close();
    }
}
