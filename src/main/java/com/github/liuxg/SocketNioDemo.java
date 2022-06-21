package com.github.liuxg;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author xinguai.liu
 */
public class SocketNioDemo {

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            //类似于epoll中的epoll_create->fd2
            Selector selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            //socket()->fd1
            serverSocketChannel.bind(new InetSocketAddress(8080));
            //epoll_ctl(fd2,ADD,fd1,ACCEPT)
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                //epoll_wait(timeout)
                while (selector.select(500) > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        if (selectionKey.isAcceptable()) {
                            handleConnect(selectionKey,selector);
                        } else if (selectionKey.isReadable()) {
                            handleRead(selectionKey);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRead(SelectionKey selectionKey) {
        SocketChannel channel = (SocketChannel)selectionKey.channel();
        ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
        try {
            channel.read(buffer);
            System.out.println("client data : "+new String(buffer.array()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void handleConnect(SelectionKey selectionKey, Selector selector) {
        SelectableChannel channel = selectionKey.channel();
        try {
            channel.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(8196);
            channel.register(selector,SelectionKey.OP_READ,buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
