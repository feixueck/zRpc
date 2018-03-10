//package com.zuosh.rpc.server;
//
//import com.zuosh.rpc.client.ZkClient;
//import com.zuosh.rpc.common.ZConstants;
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.utils.ZKPaths;
//
//public class Main {
//    public static void main(String[] args) throws Exception {
//        //
//        Zk zk = new ZkServer();
//        //
//        ZkClient client = zk.startService();
//        //handle node
//        //thread .sleep
//        Thread.sleep(3000);
//        for (int i = 0; i < 10; i++) {
//            String s = ZKPaths.makePath(ZConstants.ZRPC_PARENT_PATH, "" + i);
//            client.create().creatingParentsIfNeeded().forPath(s, (i + " service").getBytes());
//            //
//            client.setData().forPath(s, (i + "service.update ").getBytes());
//        }
//
//        //update
//        for (int i = 0; i < 10; i++) {
//            String s = ZKPaths.makePath(ZConstants.ZRPC_PARENT_PATH, "" + i);
//            client.delete().guaranteed().forPath(s);
//            //
//        }
//    }
//}
