package com.github.liuxg;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO
 * @author xinguai.liu
 */
public class SocketBlockDemo {

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(()->{
                        try {
                            InputStream inputStream = socket.getInputStream();
                            System.out.println("socket accept size : "+inputStream.available());
                            System.out.println("Socket accept data : "+toString(inputStream));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String toString(InputStream inputStream) {
        byte[] buffer = new byte[8196];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = -1;
        while (true) {
            try {
                if (((len = inputStream.read(buffer,0,buffer.length)) <= 0)) {
                    break;
                }
                baos.write(buffer,0,len);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new String(baos.toByteArray());
    }


}
