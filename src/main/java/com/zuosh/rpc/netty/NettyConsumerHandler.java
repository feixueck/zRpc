package com.zuosh.rpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyConsumerHandler extends ChannelInboundHandlerAdapter {
    private RpcHandler rpcHandler;
    //

    public NettyConsumerHandler(RpcHandler rpcHandler) {
        this.rpcHandler = rpcHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //收到消息 并做相关的处理
        NettyChannel.addChannel(ctx.channel());
        rpcHandler.receive(NettyChannel.getChannel(ctx.channel()), msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        NettyChannel.removeChannel(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        NettyChannel.removeChannel(ctx.channel());
    }
}
