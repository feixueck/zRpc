package com.zuosh.rpc.protocol;

import com.zuosh.rpc.server.ServiceUrl;

public interface Protocol {
    <T> Protocol export(String interfaceName, Class<T> tClass);

    //
    <T> Invoker refer(Class<T> tClass, ServiceUrl<T> url);

    //start
    void openServer();
}
