package com.github.liuxg;

/**
 * @author xinguai.liu
 */
public class SyncDemo {

    public static void main(String[] args) {
        Object lock = new Object();
        synchronized (lock) {
            int i = 0;
        }
    }

}
