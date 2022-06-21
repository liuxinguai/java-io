package com.github.liuxg;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author xinguai.liu
 */
public class EpollServer {

    private final int port;

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private Object lock = new Object();

    private volatile boolean started = false;

    public EpollServer(int port) {
        this.port = port;
    }


    protected void init() {
        if (started) {
            return;
        }
        synchronized (lock) {
            if (!started) {
                try {
                    //此处调用了操作系统的socket函数fd1
                    serverSocketChannel = ServerSocketChannel.open();
                    //设置这个socket函数的状态
                    serverSocketChannel.configureBlocking(false);
                    //调用了操作系统bind(fd1,port)、listen(fd1.....)
                    serverSocketChannel.bind(new InetSocketAddress(port));
                    System.out.println("server listen "+port);
                    //此处调用了操作系统epoll_create->fd2
                    selector = Selector.open();
                    //此处调用了操作系统epoll_etl(fd2,ADD,fd1,ACCEPT)
                    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                } catch (IOException e) {
                    e.printStackTrace();
                    started = false;
                }
            }
        }
        started = true;
    }

    public void start() {
        init();
        if (!started) {
            throw new RuntimeException("启动服务异常！");
        }
        while (true) {
            while (true) {
                try {
                    //此处调用了操作系统epoll_wait(fd1....)
                    if ((selector.select(50) < 0)) {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (selectionKey.isAcceptable()) {
                        handleAccept(selectionKey);
                    } else if (selectionKey.isReadable()){
                        //selection.key()相当于将出了fd1文件描述符以外的fd都移除了fd2的红黑树结构中
                        handleRead(selectionKey);
                    }
                }
            }
        }
    }

    protected void handleAccept(SelectionKey selectionKey) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        try {
            //此处调用accept(fd1....)->fd3
            SocketChannel channel = serverSocketChannel.accept();
            System.out.println("Server accept client port "+channel.socket().getPort());
            channel.configureBlocking(false);
            channel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(10));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void handleRead(SelectionKey selectionKey) {
        ByteBuffer buffer = (ByteBuffer)selectionKey.attachment();
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        List<Byte> cache = new ArrayList<>();
        try {
            int read = - 1;
            while ((read = channel.read(buffer)) > 0) {
                buffer.flip();
                for (int i = 0; i < read; i++) {
                    cache.add(buffer.get());
                }
                buffer.clear();
            }
            byte[] bytes = new byte[cache.size()];
            for (int i = 0; i < cache.size(); i++) {
                bytes[i] = cache.get(i);
            }
            System.out.println("Server receive data : "+new String(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            selectionKey.cancel();
        }
//        new Thread(()->{
//        }).start();
    }


    public static void main(String[] args) {
        EpollServer server = new EpollServer(8080);
        server.start();
    }

}
