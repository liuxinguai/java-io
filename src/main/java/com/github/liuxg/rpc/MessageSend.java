package com.github.liuxg.rpc;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xinguai.liu
 */
public class MessageSend {

    private static final ConcurrentHashMap<Long, CompletableFuture<Object>> sendCache = new ConcurrentHashMap<>();

    public static Object send(PackageMessage packageMessage) throws Exception {
        CompletableFuture<Object> future = new CompletableFuture<>();
        sendCache.putIfAbsent(packageMessage.getHeader().getRequestId(),future);
        NioSocketChannel socketChannel = ConnectionFactory.getFactory().createChannel(new InetSocketAddress("127.0.0.1", 8080));
        ChannelFuture channelFuture = socketChannel.writeAndFlush(Unpooled.copiedBuffer(PackageMessage.serializateMessage(packageMessage)));
        channelFuture.sync();
        return future.get();
    }

    public static void recevie(PackageMessage packageMessage) {
        CompletableFuture<Object> future = sendCache.get(packageMessage.getHeader().getRequestId());
        future.complete(packageMessage.getContent().getReturnData());
        sendCache.remove(packageMessage.getHeader().getRequestId());
    }

}
