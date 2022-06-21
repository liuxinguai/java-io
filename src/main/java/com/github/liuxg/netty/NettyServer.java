package com.github.liuxg.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @author xinguai.liu
 */
public class NettyServer {

    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyServer(8080).bind();
    }

    public void bind() throws InterruptedException {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
        NioServerSocketChannel nioServerSocketChannel = new NioServerSocketChannel();
        eventLoopGroup.register(nioServerSocketChannel);
        ChannelPipeline channelPipeline = nioServerSocketChannel.pipeline();
        channelPipeline.addLast(new MyAcceptHandle(eventLoopGroup,new MyInit()));
        ChannelFuture channelFuture = nioServerSocketChannel.bind(new InetSocketAddress(port));
        channelFuture.sync().channel().closeFuture();
    }

    static class MyAcceptHandle extends ChannelInboundHandlerAdapter {

        private final NioEventLoopGroup nioEventLoopGroup;

        private final ChannelHandler channelHandler;

        public MyAcceptHandle(NioEventLoopGroup eventLoopGroup,ChannelHandler channelHandler) {
            this.nioEventLoopGroup = eventLoopGroup;
            this.channelHandler = channelHandler;
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("将NioServerSocket注入到Selector中");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            NioSocketChannel channel = (NioSocketChannel) msg;
            System.out.println("Server accept : "+channel.remoteAddress().getPort());
            channel.pipeline().addLast(channelHandler);
            this.nioEventLoopGroup.register(channel);
        }
    }

    static class MyInit extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            Channel channel = ctx.channel();
            channel.pipeline().addLast(new MyHandle());
            channel.pipeline().remove(this);
        }

    }

    static class MyHandle extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            CharSequence charSequence = buf.getCharSequence(0, buf.readableBytes(), StandardCharsets.UTF_8);
            System.out.println("Server accept data : "+charSequence.toString());
            Thread.sleep(1000);
            ctx.writeAndFlush(buf);
        }
    }

}
