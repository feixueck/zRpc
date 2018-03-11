//package com.zuosh.rpc.samples;
//
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.ServerSocketChannel;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import io.netty.handler.codec.string.StringDecoder;
//import io.netty.handler.codec.string.StringEncoder;
//import jdk.nashorn.internal.runtime.linker.Bootstrap;
//
//public class NettyTest {
//
//    public static void main(String[] args) throws InterruptedException {
//        ServerBootstrap bootstrap = new ServerBootstrap();
//        //
//        EventLoopGroup boss = new NioEventLoopGroup();
//        EventLoopGroup worker = new NioEventLoopGroup(1);
//        //
//        bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
//                .option(ChannelOption.SO_BACKLOG, 1024)
//                .childHandler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new StringDecoder());
//                        ch.pipeline().addLast(new StringEncoder());
//                    }
//                });
//
//        ChannelFuture channelFuture = bootstrap.bind(2223).sync();
//
//        System.out.println(" == start ,Listen 2223 .");
//    }
//}
