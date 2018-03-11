package com.zuosh.rpc.netty;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * 解码器
 */
public class ObjectDecoderHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] bytes = new byte[in.readableBytes()];
        //
        in.readBytes(bytes);
        //
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        //
        in.clear();
        //
        Object o = inputStream.readObject();
        out.add(o);
    }
}
