package com.zuosh.rpc.server;

import com.zuosh.rpc.client.ZkClient;
import org.apache.curator.framework.CuratorFramework;

public interface Zk {
    void register(String url);

    void unRegister(String url);

    <T> void subscribe(ServiceUrl<T> url);

    ZkClient startService();
}
