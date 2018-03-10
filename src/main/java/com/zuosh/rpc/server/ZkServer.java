package com.zuosh.rpc.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuosh.rpc.client.ZkClient;
import com.zuosh.rpc.common.ZConstants;
import com.zuosh.rpc.util.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.zookeeper.CreateMode;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ZkServer implements Zk {
    public static org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(ZkServer.class);
    private static Set<String> allProviders = Sets.newHashSet();//服务提供者
    private ZkDirectory zkDirectory = new ZkDirectory();//注册的服务目录
    private static Set<String> subscribers = Sets.newHashSet();//订阅者
    //已经订阅的服务
    public static Map<ServiceUrl, NotifyListener> zkListeners = Maps.newConcurrentMap();
    //client
    private ZkClient zkClient;

    /**
     * start my service
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        Zk zk = new ZkServer();
        zk.startService();
        //进行一些 node的操作
        System.in.read();
    }

    public ZkClient startService() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZConstants.CONNECTION_INFO)
                .sessionTimeoutMs(5000).connectionTimeoutMs(5000).retryPolicy(retryPolicy).build();
        //
        client.start();
        PathChildrenCache childrenCache = new PathChildrenCache(client, ZConstants.RPC_ROOT_PATH, true);
        //
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                String path = null;
                if (event.getData() != null) {
                    path = event.getData().getPath();
                }
                LOGGER.info("path :{} event: {}", path, event.getType().name());
                switch (event.getType()) {
                    case CHILD_ADDED:
                        register(path);
                        break;
                    case CHILD_REMOVED:
                        unRegister(path);
                        break;
                    case CHILD_UPDATED:
                        LOGGER.info("{} 服务更新 ...", path);
                        break;
                    case CONNECTION_RECONNECTED:
                        recovery(client);
                    default:
                        break;
                }
            }
        });
        try {
            childrenCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //make client
        zkClient = new ZkClient(client, this);
        return zkClient;
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
     * 创建节点
     * 根据条件创建
     *
     * @param client
     * @param path
     * @param info
     */
    private static void createChildNode(CuratorFramework client, String path, String info) {
        //
        if (StringUtils.isEmpty(info)) {
            info = "NOP";
        }
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                    .forPath(path, info.getBytes());
        } catch (Exception e) {
            LOGGER.warn(" 节点 {} 已经存在 ,不需要再次创建", path);
        }
    }

    /**
     * 如果掉线重新注册
     *
     * @param client
     */
    private static void recovery(CuratorFramework client) {
        try {
            //重新注册
            allProviders.forEach(s -> {
                createChildNode(client, s, "recovery.Update");
                LOGGER.info("has recovery the node {}", s);
            });
            //重新订阅
            for (Map.Entry<ServiceUrl, NotifyListener> listenerEntry : zkListeners.entrySet()) {
                //
                ServiceUrl serviceUrl = listenerEntry.getKey();
                //
                LOGGER.info(" 服务 {} ,重新订阅 ...", serviceUrl.getaClass());
                listenerEntry.getValue().notify(serviceUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(String url) {
        LOGGER.info("{} 开始注册 ...", url);
        allProviders.add(url);
    }

    @Override
    public void unRegister(String url) {
        LOGGER.warn("{} 取消注册 ...", url);
        allProviders.remove(url);
        //也要取消订阅 ,因为服务端已经没有了

    }

    @Override
    public <T> void subscribe(ServiceUrl<T> url) {
        LOGGER.info("{} 开始订阅 ...", url);
        //
        zkListeners.putIfAbsent(url, zkDirectory);
        zkDirectory.notify(url);
        //
        zkClient.addListener(url);
    }
}
