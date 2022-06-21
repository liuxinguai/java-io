package com.github.liuxg.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author xinguai.liu
 */
public class MessageDecode extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= PackageMessage.HEADER_LEN) {
            byte[] bytes = new byte[PackageMessage.HEADER_LEN];
            in.getBytes(in.readerIndex(),bytes);
            MessageHeader header = (MessageHeader) PackageMessage.deserializate(bytes, 0, bytes.length);
            if (in.readableBytes() >= PackageMessage.HEADER_LEN + header.getContentLength()) {
                in.readBytes(PackageMessage.HEADER_LEN);
                bytes = new byte[header.getContentLength()];
                in.readBytes(bytes);
                MessageContent content = (MessageContent) PackageMessage.deserializate(bytes, 0, bytes.length);
                PackageMessage packageMessage = new PackageMessage();
                packageMessage.setContent(content);
                packageMessage.setHeader(header);
                out.add(packageMessage);
            }
        }
    }

}
