package com.zuosh.rpc.zookeepers;

import com.zuosh.rpc.server.ServiceUrl;
import com.zuosh.rpc.server.ZkDirectory;
import com.zuosh.rpc.server.ZkServer;

import java.io.IOException;

public class ZooClientTest {
    public static void main(String[] args) throws IOException {
        //
//        ServiceUrl<TmpDemo> serviceUrl = new ServiceUrl<>();
//        serviceUrl.setClazz(TmpDemo.class);
//        serviceUrl.setMethodName("");
//        serviceUrl.setParameters(null);
        //
        ZkServer zkServer = new ZkServer();
//        zkServer.startService();
        //
//        ZkDirectory directory = new ZkDirectory();
        //
        zkServer.subscribe(TmpDemo.class);
        //

        //
        System.in.read();
    }
}
