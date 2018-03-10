package com.zuosh.rpc.rpcs;

import com.zuosh.rpc.server.ServiceUrl;
import com.zuosh.rpc.server.ZkServer;
import com.zuosh.rpc.user.UserService;

import java.io.IOException;

public class UserConsumerTest {
    public static void main(String[] args) throws IOException {
        //
        ServiceUrl<UserService> serviceUrl = new ServiceUrl<>();
        serviceUrl.setaClass(UserService.class);
        serviceUrl.setMethodName("sayHello");
        serviceUrl.setParameters(null);
        //
        ZkServer zkServer = new ZkServer();
        zkServer.startService();
        //
        zkServer.subscribe(serviceUrl);
        //
        System.in.read();
    }
}
