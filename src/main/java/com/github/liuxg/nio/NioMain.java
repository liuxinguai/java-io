package com.github.liuxg.nio;

import java.io.IOException;

/**
 * @author xinguai.liu
 */
public class NioMain {
    public static void main(String[] args) throws IOException {
        NioEventGroup master = new NioEventGroup(3);
        NioEventGroup worker = new NioEventGroup(5);
        master.setWorkerGroup(worker);
        master.bind(8887);
        master.bind(8888);
        master.bind(8889);
    }
}
