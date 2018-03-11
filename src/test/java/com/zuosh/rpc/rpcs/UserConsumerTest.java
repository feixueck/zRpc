package com.zuosh.rpc.rpcs;

import com.zuosh.rpc.protocol.Invocation;
import com.zuosh.rpc.server.ServiceUrl;
import com.zuosh.rpc.server.ZkDirectory;
import com.zuosh.rpc.server.ZkServer;
import com.zuosh.rpc.user.UserService;

import java.io.IOException;

public class UserConsumerTest {
    public static void main(String[] args) throws IOException {
        //
        ServiceUrl<UserService> serviceUrl = new ServiceUrl<>();
        serviceUrl.setClazz(UserService.class);
        serviceUrl.setMethodName("sayHello");
        serviceUrl.setParameters(null);
        //
        ZkServer zkServer = new ZkServer();
        ZkDirectory zkDirectory = zkServer.getZkDirectory();

        //
        zkServer.subscribe(UserService.class);//订阅指定服务
        //
        for (int i = 0; i < 5; i++) {
            Invocation invocation = new Invocation();
            invocation.setServiceUrl(serviceUrl);
            invocation.setClazz(UserService.class);
            invocation.setServiceName(UserService.class.getSimpleName());
            invocation.setParameterTypes(new Class[]{String.class});
            invocation.setMethodName("sayHello");
            invocation.setArguments(new Object[]{"thx,judy" + i});
            //
            try {
                Object invoke = zkDirectory.getInvoker(serviceUrl).invoke(invocation);
                System.out.println(invoke);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //
        System.in.read();
    }
}
