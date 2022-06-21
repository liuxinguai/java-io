package com.github.liuxg.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xinguai.liu
 */
public class BootstrapClientDemo {

    private static final AtomicInteger count = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture channelFuture = bootstrap.group(new NioEventLoopGroup(1))
                .channel(NioSocketChannel.class)
                .handler(new ReadHandle())
                .connect("127.0.0.1", 8080);
        Channel channel = channelFuture.sync().channel();
        channel.writeAndFlush(Unpooled.copiedBuffer("hello word netty".getBytes(StandardCharsets.UTF_8))).sync();
        channel.closeFuture().sync();
        System.out.println("main closed");

    }

    static class ReadHandle extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (count.incrementAndGet() <= 5) {
                ByteBuf buf = (ByteBuf) msg;
                CharSequence charSequence = buf.getCharSequence(0, buf.readableBytes(), StandardCharsets.UTF_8);
                System.out.println("client accept data : "+charSequence.toString());
                Thread.sleep(1000);
                ctx.writeAndFlush(buf);
                return;
            }
            System.out.println("client close");
            ctx.channel().disconnect();
            System.out.println("client status : "+ctx.channel().isActive()+" , "+ctx.channel().isOpen());
            ctx.channel().closeFuture();
            System.out.println("closed");
        }
    }

}
