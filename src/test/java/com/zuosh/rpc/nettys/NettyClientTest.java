package com.zuosh.rpc.nettys;

import com.zuosh.rpc.common.ZConstants;
import com.zuosh.rpc.netty.NettyChannel;
import com.zuosh.rpc.netty.NettyClient;
import com.zuosh.rpc.netty.RpcHandler;
import com.zuosh.rpc.protocol.Request;

public class NettyClientTest {
    public static void main(String[] args) {
        RpcHandler handler = new RpcHandler() {
            @Override
            public void receive(NettyChannel nettyChannel, Object object) {
                System.out.println("get recv event");
            }

            @Override
            public void send(NettyChannel nettyChannel, Object msg) {
                System.out.println("get send event");
            }
        };
        NettyClient client = new NettyClient(handler);
        NettyChannel connect = client.connect(ZConstants.PROVIDER_HOST, ZConstants.PROVIDER_PORT);
        //
        Request request = Request.buildStandardRequest(null, "fuck");
        connect.send(request);
        //
    }
}
