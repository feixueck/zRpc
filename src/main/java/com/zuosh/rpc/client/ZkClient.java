package com.zuosh.rpc.client;

import com.google.common.collect.Maps;
import com.zuosh.rpc.common.ZConstants;
import com.zuosh.rpc.server.NotifyListener;
import com.zuosh.rpc.server.ServiceUrl;
import com.zuosh.rpc.server.ZkServer;
import com.zuosh.rpc.util.StringUtils;
import javafx.scene.layout.BorderStrokeStyle;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.security.cert.TrustAnchor;
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
        String path = ZKPaths.makePath(ZConstants.RPC_ROOT_PATH, clazz.getName(), ZConstants.PROVIDER_ROLE);
        //
        if (StringUtils.isEmpty(info)) {
            info = "zk.client.register";
        }
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, info.getBytes());
        } catch (Exception e) {
            ZConstants.LOGGER.warn("  要注册的节点 {} 已经存在,不需要重复注册", path);
        }
    }

    //取消注册服务
    public void unRegister(Class<?> clazz) {
        String path = ZKPaths.makePath(ZConstants.RPC_ROOT_PATH, clazz.getName());
        //
        try {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {
            ZConstants.LOGGER.warn("  要删除的节点 {} 无法删除,信息 {}", path, e.getMessage());
        }
    }

    //修改服务
    public void updateClass(Class<?> clazz, String info) {
        if (StringUtils.isEmpty(info)) {
            info = "Update.data";
        }
        String path = ZKPaths.makePath(ZConstants.RPC_ROOT_PATH, clazz.getName());
        //
        try {
            client.setData().forPath(path);
        } catch (Exception e) {
            ZConstants.LOGGER.warn("  要修改的节点 {} 无法修改,信息 {}", path, e.getMessage());
        }
    }

    public void addListener(ServiceUrl serviceUrl) {
        String path = serviceUrl.getPath();
        //
        if (urlCache.get(path) == null) {
            //
            PathChildrenCache treeCache = new PathChildrenCache(client, path, true);
            treeCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    ChildData data = event.getData();
                    if (data != null) {
                        switch (event.getType()) {
                            case CHILD_ADDED:
                                ZConstants.LOGGER.info(" zkClient 监听道事件 add ,路径是 :{}", data.getPath());
                                break;
                            case CHILD_REMOVED:
                                ZConstants.LOGGER.info(" zkClient 监听道事件 remove ,路径是 :{}", data.getPath());
                                break;
                            case CONNECTION_RECONNECTED:
                                ZConstants.LOGGER.info(" zkClient 监听道事件 reconnect ,路径是 :{},发送通知", data.getPath());
                                //notify
                                //+ notify
                                NotifyListener notifyListener = server.zkListeners.get(serviceUrl);
                                if (notifyListener != null) {
                                    notifyListener.notify(serviceUrl);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            });
            try {
                treeCache.start();//开启 ,不然不会生效
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
