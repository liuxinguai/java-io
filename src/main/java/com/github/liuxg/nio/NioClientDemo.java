package com.github.liuxg.nio;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author xinguai.liu
 */
public class NioClientDemo {

    public static void main(String[] args) {
        //0 % 3 =0 1 % 3 = 1 2 % 3 = 2 3 % 3 = 0
        for (int i = 0; i < 10; i++) {
            final int j = i;
            new Thread(() -> {
                try {
                    Thread.sleep(200);
                    Socket socket = new Socket("127.0.0.1", 8887 + j % 3);
                    if (socket.isConnected()) {
                        Thread.sleep(3000);
                        int index = 0;
                        while (index++ < 3) {
                            Thread.sleep(200);
                            socket.getOutputStream().write(new String("hello worldhello world"+index).getBytes(StandardCharsets.UTF_8));
                            socket.getOutputStream().flush();
                            byte[] bytes = new byte[1024];
                            int read = socket.getInputStream().read(bytes);
                            System.out.println(Thread.currentThread()+"接受到 ： "+new String(bytes,0,read));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        }
    }

}
