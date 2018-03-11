package com.zuosh.rpc.server;

import com.zuosh.rpc.client.ZkClient;

public interface Zk {
    void registerEvent(String url);

    void unRegisterEvent(String url);

    <T> void subscribe(ServiceUrl<T> url);

    /**
     * 订阅某个类的服务
     *
     * @param clazz
     * @param <T>
     */
    <T> void subscribe(Class<T> clazz);

    ZkClient startService();
}
