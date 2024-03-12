package com.study.netty.utils;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * @author Xuyong
 */
public class CloseableFileOutputStream implements Closeable {

    private final FileOutputStream file;

    public CloseableFileOutputStream(String file) throws FileNotFoundException {
        this.file = new FileOutputStream(file);
    }

    public FileChannel getChannel() {
        return file.getChannel();
    }

    @Override
    public void close() throws IOException {
        file.close();
    }
}
