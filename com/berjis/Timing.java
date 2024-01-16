package com.berjis;

public class Timing {
    static long startTime;
    final static long threshold = 5000;

    public static long passedTime() {
        return System.currentTimeMillis() - startTime;
    }
}
