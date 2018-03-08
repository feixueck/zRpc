package com.zuosh.rpc.server;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class ZkServer {
    public static final String CONNECTION_INFO = "59.110.240.159:2181";

    public static void main(String[] args) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(CONNECTION_INFO)
                .sessionTimeoutMs(5000).connectionTimeoutMs(5000).retryPolicy(retryPolicy).build();
        //
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                System.out.println("get state envert ==> " + connectionState.name());
                if (ConnectionState.SUSPENDED == connectionState) {
                    //del
                    System.err.println("conneciton lost ,please delte local");
                } else if (ConnectionState.RECONNECTED == connectionState) {
                    //add
                    recovery(client);
                    System.err.println("connection connected , recovery succ");
                }
            }
        });

        client.start();
        //;
        try {
            //create node
            createNode(client);
            //over
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void createNode(CuratorFramework client) throws Exception {
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/zRpc");
    }

    private static void recovery(CuratorFramework client) {
        try {
            createNode(client);
            System.out.println("====>recovery succ ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
