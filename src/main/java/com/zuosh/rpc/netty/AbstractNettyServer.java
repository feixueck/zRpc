package com.zuosh.rpc.netty;

public abstract class AbstractNettyServer {
    protected RpcHandler rpcHandler;

    public abstract void bind(Integer port);

    public AbstractNettyServer(RpcHandler rpcHandler) {
        this.rpcHandler = rpcHandler;
    }

}
