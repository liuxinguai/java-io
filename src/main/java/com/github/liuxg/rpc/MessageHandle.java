package com.github.liuxg.rpc;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xinguai.liu
 */
public class MessageHandle extends ChannelInboundHandlerAdapter {

    private static AtomicInteger count = new AtomicInteger(0);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        PackageMessage packageMessage = (PackageMessage) msg;
        if (packageMessage.getHeader().getFlag() == 0) {
            //TODO 此处调用后面服务器端的任务处理,此处可以根据转入的方法参数列表和类去调用具体的实现得到对应的returnData
            packageMessage.getHeader().setFlag(1);
            packageMessage.getContent().setReturnData(Thread.currentThread().getName()+"处理"+count.incrementAndGet());
            ctx.writeAndFlush(Unpooled.copiedBuffer(PackageMessage.serializateMessage(packageMessage)));
        } else {
            MessageSend.recevie(packageMessage);
        }
    }
}
