package com.github.liuxg.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xinguai.liu
 */
public class NioEventGroup {

    private final int threadNum;

    private final AtomicInteger servers = new AtomicInteger(0);

    private final NioEventRunner[] nioEventRunners;

    private NioEventGroup workerGroup = null;

    public NioEventGroup(int threadNum, NioEventGroup workerGroup) throws IOException {
        this.threadNum = threadNum;
        nioEventRunners = new NioEventRunner[threadNum];
        for (int i = 0; i < threadNum; i++) {
            nioEventRunners[i] = new NioEventRunner(this);
            new Thread(nioEventRunners[i]).start();
        }
        this.workerGroup = workerGroup;
    }

    public NioEventGroup(int threadNum) throws IOException {
        this(threadNum,null);
    }

    public void setWorkerGroup(NioEventGroup workerGroup) {
        this.workerGroup = workerGroup;
    }

    public void bind(int port) {
        try {
            ServerSocketChannel socketChannel = ServerSocketChannel.open();
            socketChannel.bind(new InetSocketAddress(port));
            socketChannel.configureBlocking(false);
            NioEventRunner nioEventRunner = nextOwn();
            nioEventRunner.put(socketChannel);
            nioEventRunner.wakeUp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NioEventRunner nextOwn() {
        return nioEventRunners[servers.incrementAndGet() % threadNum];
    }

    public NioEventRunner nextWorker() {
        if (workerGroup != null) {
            return workerGroup.nioEventRunners[workerGroup.servers.incrementAndGet() % workerGroup.threadNum];
        }
        return nextOwn();
    }


}
