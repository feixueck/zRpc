package com.zuosh.rpc.netty;

import com.zuosh.rpc.common.ZConstants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyProviderHandler extends ChannelInboundHandlerAdapter {
    private RpcHandler rpcHandler;
    //

    public NettyProviderHandler(RpcHandler rpcHandler) {
        this.rpcHandler = rpcHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //
        ZConstants.LOGGER.info(" provider recv msg: {}", msg.getClass());
        NettyChannel.addChannel(ctx.channel());
        rpcHandler.receive(NettyChannel.getChannel(ctx.channel()), msg);
        //
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel.removeChannel(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        NettyChannel.removeChannel(ctx.channel());
    }
}
