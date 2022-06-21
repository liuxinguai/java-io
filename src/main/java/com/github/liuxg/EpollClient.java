package com.github.liuxg;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author xinguai.liu
 */
public class EpollClient {

    public static void main(String[] args) {
        int tmp = 0;
        try {
            Socket socket = new Socket("127.0.0.1",8080);
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                System.out.println(socket.isConnected());
                socket.getOutputStream().write(new String("hello wolrdhello wolrd"+i).getBytes(StandardCharsets.UTF_8));
                socket.getOutputStream().flush();
                System.out.println("发送成功！"+(tmp++));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println(tmp);
        }
    }

}
