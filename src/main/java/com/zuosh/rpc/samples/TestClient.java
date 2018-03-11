package com.zuosh.rpc.samples;

import com.zuosh.rpc.common.ZConstants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class TestClient {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ObjectEncoder(), new ObjectDecoder(ClassResolvers.cacheDisabled(null))
                                    , new TestClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(ZConstants.PROVIDER_HOST, ZConstants.PROVIDER_PORT).sync();
            //shutdown
            channelFuture.channel().closeFuture().sync();
            //
        } catch (InterruptedException e) {
//            e.printStackTrace();
            group.shutdownGracefully();
        }
    }
}
