package com.zuosh.rpc.netty;

public abstract class AbstractRpcHandler implements RpcHandler {
    @Override
    public void send(NettyChannel channel, Object msg) {
        channel.send(msg);
    }
}
