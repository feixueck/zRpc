package com.zuosh.rpc.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuosh.rpc.client.ZkClient;
import com.zuosh.rpc.common.ZConstants;
import com.zuosh.rpc.util.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * 发布端和订阅端可以共用一个类
 * 发布包含 发布的集合providers
 * 订阅忧订阅的集合
 * 当遇到重连时间的时候,根据各自的集合是否为空 ,来判断重连的内容
 */
public class ZkServer implements Zk {
    public static Logger LOGGER = LoggerFactory.getLogger(ZkServer.class);
    private static Set<String> allProviders = Sets.newHashSet();//服务提供者
    private ZkDirectory zkDirectory = new ZkDirectory();//注册的服务目录
    private static Set<String> subscribers = Sets.newHashSet();//订阅者
    //已经订阅的服务
    public static Map<ServiceUrl, NotifyListener> zkListeners = Maps.newConcurrentMap();
    //client
    private ZkClient zkClient;
    private CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZConstants.CONNECTION_INFO)
            .sessionTimeoutMs(5000).connectionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

//    /**
//     * `
//     * start my service
//     *
//     * @param args
//     */
//    public static void main(String[] args) throws IOException {
//        Zk zk = new ZkServer();
//        zk.startService();
//        //进行一些 node的操作
//        System.in.read();
//    }

    /**
     * 创建对象的时候进行构造
     */
    public ZkServer() {
        client.start();
        //
        zkClient = new ZkClient(client, this);
    }

    /**
     * 启动服务返回zkClient
     *
     * @return
     */
    public ZkClient startService() {
        //

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
                        registerEvent(path);
                        break;
                    case CHILD_REMOVED:
                        unRegisterEvent(path);
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
        return zkClient;
    }


    /**
     * 创建节点 [用于断线重连]
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
            //重新注册 (发布端)
            if (allProviders.size() > 0) {
                allProviders.forEach(s -> {
                    createChildNode(client, s, "recovery.Update");
                    LOGGER.info(" 服务 {} ,重新注册 ...", s);
                });
            }

            //重新订阅(订阅端)
            for (Map.Entry<ServiceUrl, NotifyListener> listenerEntry : zkListeners.entrySet()) {
                //
                ServiceUrl serviceUrl = listenerEntry.getKey();
                //
                LOGGER.info(" 服务 {} ,重新订阅 ...", serviceUrl.getClazz());
                listenerEntry.getValue().notify(serviceUrl, NotifyListener.NotifyEvent.add);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerEvent(String url) {
        LOGGER.info("{} 注册时间进入 ...", url);
        allProviders.add(url);
    }

    @Override
    public void unRegisterEvent(String url) {
        LOGGER.warn("{} 取消注册事件进入 ...", url);
        allProviders.remove(url);
        //也要取消订阅 ,因为服务端已经没有了
    }

    @Override
    public <T> void subscribe(ServiceUrl<T> url) {
        LOGGER.info("{} 开始订阅 ...", url);
        //
        zkListeners.putIfAbsent(url, zkDirectory);
        zkDirectory.notify(url, NotifyListener.NotifyEvent.add);
        //
        zkClient.addListener(url);
    }

    @Override
    public <T> void subscribe(Class<T> clazz) {
        //
        ServiceUrl serviceUrl = new ServiceUrl();
        serviceUrl.setClazz(clazz);
        //通知目录服务,创建invoker
        zkDirectory.notify(serviceUrl, NotifyListener.NotifyEvent.add);
        //加入监听集合,保证断线重连
        zkListeners.putIfAbsent(serviceUrl, zkDirectory);
        //给当前节点加一个
        zkClient.addListener(serviceUrl);
        ZConstants.LOGGER.info(" 订阅服务 {}, ok ...", clazz.getName());
    }
    //

    public ZkDirectory getZkDirectory() {
        return zkDirectory;
    }
}
