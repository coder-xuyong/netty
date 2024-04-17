package com.study.netty.c1;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OftenTest {
    public static void main(String[] args) {
        HashMap<Object, Object> map = new HashMap<>();
        Object isOctave = map.getOrDefault("isOctave","0");
        isOctave = Integer.parseInt(isOctave.toString());
        System.out.println(isOctave);

    }
}
