package com.zuosh.rpc.client;

import com.zuosh.rpc.common.ZConstants;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * demo from
 * http://blog.csdn.net/haoyuyang/article/details/53469269
 * test zk client demo
 * Created by zuoshuai on 2018/3/8.
 */
public class ZkClient {

    public static void main(String[] args) throws Exception {
        //
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 10);
        CuratorFramework curator = CuratorFrameworkFactory.builder().connectString(ZConstants.CONNECTION_INFO)
                .sessionTimeoutMs(ZConstants.SESSION_TIMEOUT).retryPolicy(policy).build();
        curator.start();
//        nodeListenerCache();
        //第三个参数表示是否接收节点数据内容  
        PathChildrenCache childrenCache = new PathChildrenCache(curator, ZConstants.ZRPC_TEST_PATH, true);
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        childrenCache.getListenable().addListener((framework, event) -> {
            switch (event.getType()) {
                case CHILD_ADDED:
                    System.out.println("CHILD_ADDED，类型：" + event.getType() + "，路径：" + event.getData().getPath() + "，数据：" +
                            new String(event.getData().getData()) + "，状态：" + event.getData().getStat());
                    break;
                case CHILD_UPDATED:
                    System.out.println("CHILD_UPDATED，类型：" + event.getType() + "，路径：" + event.getData().getPath() + "，数据：" +
                            new String(event.getData().getData()) + "，状态：" + event.getData().getStat());
                    break;
                case CHILD_REMOVED:
                    System.out.println("CHILD_REMOVED，类型：" + event.getType() + "，路径：" + event.getData().getPath() + "，数据：" +
                            new String(event.getData().getData()) + "，状态：" + event.getData().getStat());
                    break;
                default:
                    break;
            }
        });
        //clean evn
        Stat stat = curator.checkExists().forPath(ZConstants.ZRPC_TEST_PATH);
        if (stat != null) {
            curator.delete().deletingChildrenIfNeeded().forPath(ZConstants.ZRPC_TEST_PATH);
        }
        //
        curator.create().forPath(ZConstants.ZRPC_TEST_PATH, "123".getBytes());
        curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(ZConstants.ZRPC_TEST_PATH + "/c1", "c1内容".getBytes());
        //经测试，不会监听到本节点的数据变更，只会监听到指定节点下子节点数据的变更  
        curator.setData().forPath(ZConstants.ZRPC_TEST_PATH, "456".getBytes());
        curator.setData().forPath(ZConstants.ZRPC_TEST_PATH + "/c1", "c1新内容".getBytes());
        curator.delete().guaranteed().deletingChildrenIfNeeded().forPath(ZConstants.ZRPC_TEST_PATH);
        System.in.read();
    }

    private static void nodeListenerCache(CuratorFramework curator) throws Exception {

        //最后一个参数表示是否进行压缩  
        NodeCache cache = new NodeCache(curator, ZConstants.ZRPC_TEST_PATH + "/super", false);
        cache.start(true);
        //只会监听节点的创建和修改，删除不会监听  
        cache.getListenable().addListener(() -> {
            if (cache.getCurrentData().getData() != null) {
                System.out.println("路径：" + cache.getCurrentData().getPath());
                System.out.println("数据：" + new String(cache.getCurrentData().getData()));
                System.out.println("状态：" + cache.getCurrentData().getStat());
            }
        });
        //delete first
        curator.delete().deletingChildrenIfNeeded().forPath(ZConstants.ZRPC_TEST_PATH);
        //test create
        curator.create().creatingParentsIfNeeded().forPath(ZConstants.ZRPC_TEST_PATH + "/super", "1234".getBytes());
        Thread.sleep(1000);
        curator.setData().forPath(ZConstants.ZRPC_TEST_PATH + "/super", "5678".getBytes());
        Thread.sleep(1000);
        curator.delete().deletingChildrenIfNeeded().forPath(ZConstants.ZRPC_TEST_PATH);
        Thread.sleep(5000);
        curator.close();
    }
}
