package com.zuosh.rpc.client;

import com.google.common.collect.Maps;
import com.zuosh.rpc.common.ZConstants;
import com.zuosh.rpc.server.NotifyListener;
import com.zuosh.rpc.server.ServiceUrl;
import com.zuosh.rpc.server.ZkServer;
import com.zuosh.rpc.util.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.CreateMode;

import java.util.Map;

/**
 * 用来给 zkserver 提供操作工具
 * demo from
 * http://blog.csdn.net/haoyuyang/article/details/53469269
 * test zk client demo
 * Created by zuoshuai on 2018/3/8.
 */
public class ZkClient {
    private CuratorFramework client;
    private ZkServer server;
    //    public static final String RPC_ROOT_PATH = ZKPaths.makePath(ZConstants.ZRPC_PARENT_PATH, "my");
    private static Map<ServiceUrl, PathChildrenCacheListener> urlCache = Maps.newConcurrentMap();

    public ZkClient(CuratorFramework client, ZkServer server) {
        this.client = client;
        this.server = server;
    }

    //注册服务
    public void register(Class<?> clazz, String info) {
//        String path = ZKPaths.makePath(ZConstants.RPC_ROOT_PATH, clazz.getName(), ZConstants.PROVIDER_ROLE);
        String pathOfClazz = ZConstants.getPathOfClazz(clazz, ZConstants.PROVIDER_ROLE);
        //
        if (StringUtils.isEmpty(info)) {
            info = "zk.client.register";
        }
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(pathOfClazz, info.getBytes());
        } catch (Exception e) {
            ZConstants.LOGGER.warn("  要注册的节点 {} 已经存在,不需要重复注册", pathOfClazz);
        }
    }

    //取消注册服务
    public void unRegister(Class<?> clazz) {
//        String path = ZKPaths.makePath(ZConstants.RPC_ROOT_PATH, clazz.getName());
        String pathOfClazz = ZConstants.getPathOfClazz(clazz, null);
        //
        try {
            client.delete().deletingChildrenIfNeeded().forPath(pathOfClazz);
        } catch (Exception e) {
            ZConstants.LOGGER.warn("  要删除的节点 {} 无法删除,信息 {}", pathOfClazz, e.getMessage());
        }
    }

    //修改服务
    public void updateClass(Class<?> clazz, String info) {
        if (StringUtils.isEmpty(info)) {
            info = "Update.data";
        }
//        String path = ZKPaths.makePath(ZConstants.RPC_ROOT_PATH, clazz.getName());
        String pathOfClazz = ZConstants.getPathOfClazz(clazz, ZConstants.PROVIDER_ROLE);
        //
        try {
            client.setData().forPath(pathOfClazz);
        } catch (Exception e) {
            ZConstants.LOGGER.warn("  要修改的节点 {} 无法修改,信息 {}", pathOfClazz, e.getMessage());
        }
    }

    public void addListener(ServiceUrl serviceUrl) {
        String path = serviceUrl.getPath();
        ZConstants.LOGGER.info("zkClient 把 path={} 加入监听 ...", path);
        //
        if (urlCache.get(path) == null) {
            //
            PathChildrenCache childrenCache = new PathChildrenCache(client, path, true);
            try {
                childrenCache.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //// TODO: 2018/3/11 监控指定节点 ,那么如果该节点删除,则要删掉invoker 
            childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    ChildData data = event.getData();
                    if (data != null) {
                        switch (event.getType()) {
                            //有可能是断掉的服务重新连接 ,so ,需要再次加入 目录服务
                            case CHILD_ADDED:
                                ZConstants.LOGGER.info(" zkClient 监听到事件 add ,路径是 :{}", data.getPath());
                                //
                                server.zkListeners.putIfAbsent(serviceUrl, server.getZkDirectory());
                                NotifyListener listener = server.zkListeners.get(serviceUrl);
                                if (listener != null) {
                                    listener.notify(serviceUrl, NotifyListener.NotifyEvent.add);
                                }
                                //
                                break;
                            case CHILD_REMOVED:
                                ZConstants.LOGGER.info(" zkClient 监听到事件 remove ,路径是 :{} ,从订阅列表删掉", data.getPath());
                                //此时应当删除订阅的服务
                                NotifyListener removeListener = server.zkListeners.get(serviceUrl);
                                removeListener.notify(serviceUrl, NotifyListener.NotifyEvent.remove);
                                server.zkListeners.remove(serviceUrl);
                                ZConstants.LOGGER.info("zkClient 删除后,剩余的节点: {}", server.zkListeners.size());
                                // TODO: 2018/3/10 目录列表中删除
                                break;
                            case CHILD_UPDATED:
                                //更新暂时不需要什么操作
                                ZConstants.LOGGER.info(" zkClient 监听到事件 update ,路径是 :{} ,从订阅列表更新", data.getPath());
                                break;
                            case CONNECTION_RECONNECTED:
                                ZConstants.LOGGER.info(" zkClient 监听到事件 reconnect ,路径是 :{},发送通知", data.getPath());
                                //notify
                                //+ notify
                                NotifyListener notifyListener = server.zkListeners.get(serviceUrl);
                                if (notifyListener != null) {
                                    notifyListener.notify(serviceUrl, NotifyListener.NotifyEvent.add);
                                }
                                break;
                            default:
                                break;
                        }
                    } else {
                        ZConstants.LOGGER.info("zkClient 监听到不支持的事件: {}", event.getType().name());
                    }
                }
            });
        }
    }


//    public void addListenerTreeCache(ServiceUrl serviceUrl) {
//        String path = serviceUrl.getPath();
//        ZConstants.LOGGER.info("zkClient 把 path={} 加入监听 ...", path);
//        //
//        if (urlCache.get(path) == null) {
//            TreeCache treeCache = new TreeCache(client, path);
//            try {
//                treeCache.start();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            //
//            TreeCacheListener treeCacheListener = new TreeCacheListener() {
//                @Override
//                public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
//                    switch (event.getType()) {
//                        case NODE_ADDED:
//                            System.out.println("Node add " + ZKPaths.getNodeFromPath(event.getData().getPath()));
//                            break;
//                        case NODE_REMOVED:
//                            System.out.println("Node removed " + ZKPaths.getNodeFromPath(event.getData().getPath()));
//                            break;
//                        case NODE_UPDATED:
//                            System.out.println("Node updated " + ZKPaths.getNodeFromPath(event.getData().getPath()));
//                            break;
//                    }
//                }
//            };
//            treeCache.getListenable().addListener(treeCacheListener);
//        }
//    }
}
