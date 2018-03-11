package com.zuosh.rpc.netty;

import com.zuosh.rpc.common.ZConstants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer extends AbstractNettyServer {
    ServerBootstrap bootstrap = new ServerBootstrap();
    //
    // Configure the server.
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Override
    public void bind(Integer port) {
        //
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                        ch.pipeline().addLast(new ObjectDecoderHandler());
                        ch.pipeline().addLast(new ObjectEncoderHandler());
                        ch.pipeline().addLast(new NettyProviderHandler(rpcHandler));
                    }
                });
        try {
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            ZConstants.LOGGER.info("Netty Sever start at port {} ,in service .", port);
            //
//            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public NettyServer(RpcHandler rpcHandler) {
        super(rpcHandler);
    }

//    public static void main(String[] args) throws InterruptedException {
//        //
//        //set log
////        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
//
//        new NettyServer(new RpcHandler() {
//            @Override
//            public Response receive(Channel channel, Object object) {
//                System.out.println("==> server recv packet :" + object + "==>" + object.getClass());
//                //
//                return Response.buildResponse(1L,null, "i am a response ");
//            }
//
//            @Override
//            public void send(Channel channel, Object msg) {
//                System.out.println("==> write to response:" + msg);
//                System.out.println(channel.writeAndFlush(msg));
//            }
//        }).bind(ZConstants.PROVIDER_PORT);
//    }
}
