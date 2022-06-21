package com.github.liuxg;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * nio非多路复用版本
 * @author xinguai.liu
 */
public class SocketNonBlockDemo {

    public static void main(String[] args) {
        List<SocketChannel> socketChannels = new ArrayList<>(1024);
        ByteBuffer buffer = ByteBuffer.allocate(8196);
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(8080), 2);
            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    socketChannel.configureBlocking(false);
                    socketChannels.add(socketChannel);
                    System.out.println(" client : "+socketChannel.socket().getRemoteSocketAddress().toString());
                }
                socketChannels.forEach(channel -> {
                    try {
                        socketChannel.read(buffer);
                        System.out.println("client data :"+new String(buffer.array()));
                        buffer.flip();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
