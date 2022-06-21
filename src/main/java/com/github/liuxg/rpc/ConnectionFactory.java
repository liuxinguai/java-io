package com.github.liuxg.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xinguai.liu
 */
public class ConnectionFactory {

    private final static ConnectionFactory FACTORY = new ConnectionFactory();

    private ConnectionFactory() {}

    private final static ConcurrentHashMap<InetSocketAddress,ConnectionPool> cacheNios = new ConcurrentHashMap<>();

    public static ConnectionFactory getFactory() {
        return FACTORY;
    }

    public NioSocketChannel createChannel(InetSocketAddress inetSocketAddress) {
        ConnectionPool connectionPool = cacheNios.get(inetSocketAddress);
        if (connectionPool == null) {
            synchronized (FACTORY) {
                if (connectionPool == null) {
                    connectionPool = new ConnectionPool(4);
                    cacheNios.putIfAbsent(inetSocketAddress,connectionPool);
                }
            }
        }
        int next = connectionPool.next();
        synchronized (connectionPool.lock[next]) {
            if (connectionPool.channels[next] == null) {
                try {
                    connectionPool.channels[next] = newConnection(inetSocketAddress, connectionPool.nioEventLoopGroup);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return connectionPool.channels[next];
    }


    protected NioSocketChannel newConnection(InetSocketAddress inetSocketAddress, NioEventLoopGroup nioEventLoopGroup) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture channelFuture = bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) {
                        channel.pipeline()
                                .addLast(new MessageDecode())
                                .addLast(new MessageHandle());
                    }
                }).connect(inetSocketAddress);
        return (NioSocketChannel) channelFuture.sync().channel();
    }


    class ConnectionPool {

        private final int num;

        private final NioSocketChannel[] channels;

        private final Object[] lock;


        private final Random random = new Random();

        private final NioEventLoopGroup nioEventLoopGroup;

        public ConnectionPool(int num) {
            this.num = num;
            nioEventLoopGroup = new NioEventLoopGroup(num);
            channels = new NioSocketChannel[num];
            lock = new Object[num];
            for (int i = 0; i < num; i++) {
                lock[i] = new Object();
            }
        }

        public int next() {
            return random.nextInt(num);
        }

    }



}
