package com.zuosh.rpc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    private RpcHandler rpcHandler;

    //

    public NettyClient(RpcHandler rpcHandler) {
        this.rpcHandler = rpcHandler;
    }

    //创建连接
    public NettyChannel connect(String host, int port) {
        if (rpcHandler == null) {
            throw new RuntimeException("rpcHandler can not be null");
        }
        //build client
        Bootstrap bootstrap = new Bootstrap();
        //
        NioEventLoopGroup group = new NioEventLoopGroup();
        //
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ObjectDecoderHandler());
                        ch.pipeline().addLast(new ObjectEncoderHandler());
                        ch.pipeline().addLast(new NettyConsumerHandler(rpcHandler));
                    }
                });

        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Channel channel = channelFuture.channel();
        NettyChannel.addChannel(channel);
        //
        return NettyChannel.getChannel(channel);
    }

}
