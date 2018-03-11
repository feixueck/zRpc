package com.zuosh.rpc.nettys;

import com.zuosh.rpc.common.ZConstants;
import com.zuosh.rpc.netty.NettyChannel;
import com.zuosh.rpc.netty.NettyServer;
import com.zuosh.rpc.netty.RpcHandler;

public class NettyServerTest {
    public static void main(String[] args) {
        //
        RpcHandler handler = new RpcHandler() {
            @Override
            public void receive(NettyChannel nettyChannel, Object object) {
                System.out.println(" i receive object " + object);
            }

            @Override
            public void send(NettyChannel nettyChannel, Object msg) {
                System.out.println(" send ???");
            }
        };
        NettyServer nettyServer = new NettyServer(handler);
        nettyServer.bind(ZConstants.PROVIDER_PORT);
        //
        System.out.println("Listen test ...");
    }
}
