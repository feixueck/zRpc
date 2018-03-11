package com.zuosh.rpc.zookeepers;

import com.zuosh.rpc.client.ZkClient;
import com.zuosh.rpc.common.ZConstants;
import com.zuosh.rpc.protocol.DefaultProtocol;
import com.zuosh.rpc.server.ZkServer;
import com.zuosh.rpc.user.UserService;
import com.zuosh.rpc.user.UserServiceImpl;

import java.io.IOException;

public class ZooServerTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        //
//        UserService service = new UserServiceImpl();
//        System.out.println(service.sayHello("hello"));
        ZkServer server = new ZkServer();
        ZkClient zkClient = server.startService();
        for (int i = 0; i < 2; i++) {
            //
            zkClient.register(TmpDemo.class, "this is my demo");
            //threa sleep
            Thread.sleep(2000);
            zkClient.updateClass(TmpDemo.class, "update xxxx");
            //
            Thread.sleep(1000);
            zkClient.unRegister(TmpDemo.class);
            //
            zkClient.updateClass(TmpDemo.class, "you update");
        }
        zkClient.register(TmpDemo.class, "this is my demo");
        //export 服务
        System.in.read();
    }
}
