package com.zuosh.rpc.protocol;

public interface Invoker {
    Object invoke(Invocation invocation) throws InterruptedException;
}
