package com.zuosh.rpc.netty;

public interface RpcHandler {
    void receive(NettyChannel nettyChannel, Object object);

    void send(NettyChannel nettyChannel, Object msg);
}
