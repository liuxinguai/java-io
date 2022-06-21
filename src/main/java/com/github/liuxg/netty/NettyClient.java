package com.github.liuxg.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xinguai.liu
 */
public class NettyClient {

    private final String hostname;

    private final int port;

    private static final AtomicInteger count = new AtomicInteger();

    public NettyClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void connect() throws InterruptedException {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(1);
        NioSocketChannel socketChannel = new NioSocketChannel();
        nioEventLoopGroup.register(socketChannel);
        socketChannel.pipeline().addLast(new MyHandle());
        ChannelFuture channelFuture = socketChannel.connect(new InetSocketAddress(hostname, port));
        Channel channel = channelFuture.sync().channel();
        ByteBuf buffer = Unpooled.copiedBuffer("hello world".getBytes(StandardCharsets.UTF_8));
        ChannelFuture writeAndFlush = channel.writeAndFlush(buffer);
        writeAndFlush.sync();
        channel.closeFuture().sync();
    }

    static class MyHandle extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (count.incrementAndGet() <= 3) {
                ByteBuf buf = (ByteBuf) msg;
                CharSequence charSequence = buf.getCharSequence(0, buf.readableBytes(), StandardCharsets.UTF_8);
                System.out.println("client accept data : "+charSequence);
                Thread.sleep(1000);
                ctx.writeAndFlush(buf);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        NettyClient client = new NettyClient("127.0.0.1",8080);
        client.connect();
    }

}
