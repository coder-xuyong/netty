package com.study.netty.c1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @author Xuyong
 */
public class TestFilesCopy {
    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        String source = "D:\\POF";
        String target = "D:\\Snipaste-1.16.2-x64aaa";

        try (Stream<Path> paths = Files.walk(Paths.get(source))) {
            paths.forEach(path -> {
                try {
                    String targetName = path.toString().replace(source, target);
                    // 是目录
                    Path targetPath = Paths.get(targetName);
                    if (Files.isDirectory(path)) {
                        Files.createDirectory(targetPath);
                    }
                    // 是普通文件
                    else if (Files.isRegularFile(path)) {
                        Files.copy(path, targetPath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}

