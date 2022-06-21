package com.github.liuxg.nio;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author xinguai.liu
 */
public class NioEventRunner implements Runnable {

    private final Selector selector;

    private final NioEventGroup nioEventGroup;

    private final BlockingQueue<Channel> blockingQueue = new LinkedBlockingQueue<>();

    public NioEventRunner(NioEventGroup nioEventGroup) throws IOException {
        this.selector = Selector.open();
        this.nioEventGroup = nioEventGroup;
    }

    @Override
    public void run() {
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
                        acceptHandle(selectionKey);
                    } else if (selectionKey.isReadable()) {
                        readHandle(selectionKey);
                    }
                }
            }
            runAllTasks();
        }
    }

    private void readHandle(SelectionKey selectionKey) {
        ByteBuffer buffer = (ByteBuffer)selectionKey.attachment();
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        System.out.println(Thread.currentThread().getName()+"接受到客户端["+socketChannel.socket().getPort()+"]写入");
        try {
            while (socketChannel.read(buffer) > 0) {
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
            selectionKey.cancel();
        }
    }

    private void runAllTasks() {
        Channel channel = blockingQueue.poll();
        if (channel != null) {
            try {
                if (channel instanceof ServerSocketChannel) {
                    ServerSocketChannel socketChannel = (ServerSocketChannel)channel;
                    System.out.println(Thread.currentThread().getName()+"监听 : "+socketChannel.socket().getLocalPort());
                    socketChannel.register(selector,SelectionKey.OP_ACCEPT);
                } else if (channel instanceof SocketChannel) {
                    SocketChannel socketChannel = (SocketChannel)channel;
                    System.out.println(Thread.currentThread().getName()+"accept client : "+socketChannel.socket().getPort());
                    socketChannel.register(selector,SelectionKey.OP_READ,ByteBuffer.allocateDirect(4096));
                }
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }
    }

    private void acceptHandle(SelectionKey selectionKey) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)selectionKey.channel();
        try {
            SocketChannel accept = serverSocketChannel.accept();
            accept.configureBlocking(false);
            System.out.println(Thread.currentThread().getName()+" accept port : "+accept.socket().getPort());
            NioEventRunner nioEventRunner = nioEventGroup.nextWorker();
            nioEventRunner.put(accept);
            nioEventRunner.wakeUp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void put(Channel channel) {
        blockingQueue.offer(channel);
    }

    public void wakeUp() {
        this.selector.wakeup();
    }


}
