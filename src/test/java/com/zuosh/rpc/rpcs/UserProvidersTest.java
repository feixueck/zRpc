package com.zuosh.rpc.rpcs;

import com.zuosh.rpc.client.ZkClient;
import com.zuosh.rpc.common.ZConstants;
import com.zuosh.rpc.protocol.DefaultProtocol;
import com.zuosh.rpc.server.ZkServer;
import com.zuosh.rpc.user.UserService;
import com.zuosh.rpc.user.UserServiceImpl;

import java.io.IOException;

public class UserProvidersTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        //
//        UserService service = new UserServiceImpl();
//        System.out.println(service.sayHello("hello"));
        ZkServer server = new ZkServer();
        ZkClient zkClient = server.startService();
        //
        zkClient.register(UserService.class, "sayHello");
        //export 服务
        new DefaultProtocol().export(UserService.class.getSimpleName()
                , UserServiceImpl.class).openServer();
        ZConstants.LOGGER.info(" Provider start ok ... ");
        //
//        Thread.sleep(2000);
//        zkClient.updateClass(UserService.class, "sayHello.update");
//        //
//        Thread.sleep(1000);
//        zkClient.unRegister(UserService.class);
//        //register again
//        Thread.sleep(1000);
        //
//        System.in.read();
    }
}
