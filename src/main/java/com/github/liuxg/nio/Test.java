package com.github.liuxg.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Test {

    public static void main(String[] args) {
        try {
            ServerSocketChannel socketChannel = ServerSocketChannel.open();
            socketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
            socketChannel.bind(new InetSocketAddress(8080));
            while (true) {
                int selected = -1;
                try {
                    selected = selector.select();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (selected > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        if (selectionKey.isAcceptable()) {
                            ServerSocketChannel serverSocketChannel = (ServerSocketChannel)selectionKey.channel();
                            SocketChannel accept = serverSocketChannel.accept();
                            accept.configureBlocking(false);
                            System.out.println(Thread.currentThread().getName()+" accept port : "+accept.socket().getPort());
                            System.out.println();
                        } else if (selectionKey.isReadable()) {
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
