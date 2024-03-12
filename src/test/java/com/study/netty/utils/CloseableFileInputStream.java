package com.study.netty.utils;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author Xuyong
 */
public class CloseableFileInputStream implements Closeable {

    private final FileInputStream file;

    public CloseableFileInputStream(String file) throws FileNotFoundException {
        this.file = new FileInputStream(file);
    }

    public FileChannel getChannel() {
        return file.getChannel();
    }

    @Override
    public void close() throws IOException {
        file.close();
    }
}
