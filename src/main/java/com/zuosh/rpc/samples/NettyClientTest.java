//package com.zuosh.rpc.samples;
//
//import com.zuosh.rpc.common.ZConstants;
//import com.zuosh.rpc.netty.*;
//import com.zuosh.rpc.protocol.Request;
//import com.zuosh.rpc.protocol.Response;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//public class NettyClientTest {
//
//    private static RpcHandler rpcHandler = new RpcHandler() {
//        @Override
//        public Response receive(Channel channel, Object object) {
//            //client receive ,just recv
//            if (object != null) {
//                System.out.print("==> get msg from server: ");
//                if (object instanceof Response) {
//                    Response response = (Response) object;
//                    System.out.println(String.format("Response[%s]", response.getMsg()));
//                }
//            }
//            return null;
//        }
//
//        @Override
//        public void send(Channel channel, Object msg) {
//            //client send
//        }
//    };
//
//    public static void main(String[] args) throws InterruptedException, IOException {
//        //
//        //set log
////        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
//        Bootstrap bootstrap = new Bootstrap();
//        //
//        NioEventLoopGroup group = new NioEventLoopGroup();
//        //
//        bootstrap.group(group).channel(NioSocketChannel.class)
//                .option(ChannelOption.SO_KEEPALIVE, true)
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new ObjectDecoderHandler());
//                        ch.pipeline().addLast(new ObjectEncoderHandler());
//                        ch.pipeline().addLast(new NettyProviderHandler(rpcHandler));
//                    }
//                });
//
//        ChannelFuture channelFuture = bootstrap.connect(ZConstants.PROVIDER_HOST, ZConstants.PROVIDER_PORT).sync();
//        Channel channel = channelFuture.channel();
//        //
////        Thread.sleep(1000);
//        while (true) {
//            NettyChannel myChannel = NettyChannel.getChannel(channel);
//            //
//            Thread.sleep(200L);
//            //
//            System.out.println("Please input your request :\n");
//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//            String s = in.readLine();
//            if (s.equals("q")) {
//                System.out.println("[ Exit ]");
//                channel.disconnect();
//                NettyChannel.removeChannel(channel);
//                return;
//            }
//            //
//            myChannel.send(channel, Request.buildRequest(s));
//        }
////
//    }
//}
