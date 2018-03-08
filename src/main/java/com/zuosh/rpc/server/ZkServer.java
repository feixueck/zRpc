package com.zuosh.rpc.server;

import com.google.common.collect.Lists;
import com.zuosh.rpc.common.ZConstants;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.logging.log4j.LogManager;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.Arrays;
import java.util.List;

public class ZkServer {
    public static org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(ZkServer.class);
    private List<String> allProviders = Lists.newArrayList();

    public static void main(String[] args) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZConstants.CONNECTION_INFO)
                .sessionTimeoutMs(5000).connectionTimeoutMs(5000).retryPolicy(retryPolicy).build();
        //
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                LOGGER.warn("get zk event {}", connectionState.name());
                if (ConnectionState.SUSPENDED == connectionState) {
                    //del
                    LOGGER.warn("zk node suspend event , {}", connectionState.name());
                } else if (ConnectionState.RECONNECTED == connectionState) {
                    //add
                    recovery(client);
                    LOGGER.info("zk node recovery succ ,{} ", connectionState.name());
                }
            }
        });
        client.start();
        //;
        try {
            //create node
            createNode(client, null);
            //over
            Thread.sleep(2000);
            //check exists
            Stat stat = client.checkExists().forPath(String.format("%s/%s", ZConstants.ZRPC_PARENT_PATH, ZConstants.VERSION));
            LOGGER.info("check exists => " + stat);
            //get data
            byte[] bytes = client.getData().forPath(String.format("%s/%s", ZConstants.ZRPC_PARENT_PATH, ZConstants.VERSION));
            LOGGER.info("get data for path =>" + new String(bytes));
            //update data
            client.setData().forPath(String.format("%s/%s", ZConstants.ZRPC_PARENT_PATH, ZConstants.VERSION), "create.Update".getBytes());
            //get new data
            LOGGER.info("get new data => {}", new String(client.getData().forPath(String.format("%s/%s"
                    , ZConstants.ZRPC_PARENT_PATH, ZConstants.VERSION))));
            //get children list
            List<String> list = client.getChildren().forPath(ZConstants.ZRPC_PARENT_PATH);
            LOGGER.info("get children list => {}", Arrays.toString(list.toArray()));
//            deleteNode();

            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除节点
     *
     * @param client
     */
    private static void deleteNode(CuratorFramework client) {
        //delete safe
        try {
            client.delete().guaranteed().deletingChildrenIfNeeded().forPath(ZConstants.ZRPC_PARENT_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.close();
        LOGGER.info("has closed ");
    }

    /**
     * 创建根节点
     *
     * @param client
     * @throws Exception
     */
    private static void createNode(CuratorFramework client, String node) throws Exception {
        //
        if (node == null) {
            node = "create.V1";
        }
        //
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                .forPath(String.format("%s/%s", ZConstants.ZRPC_PARENT_PATH, ZConstants.VERSION), node.getBytes());
        LOGGER.info("has create root node {}", client.getNamespace());
    }

    /**
     * 如果掉线重新注册
     *
     * @param client
     */
    private static void recovery(CuratorFramework client) {
        try {
            createNode(client, "recovery.V1");
            LOGGER.info("has recovery the node {}", client.getNamespace());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
