package com.zuosh.rpc.server;

import com.google.common.collect.Maps;
import com.zuosh.rpc.common.ZConstants;
import com.zuosh.rpc.protocol.Invocation;
import com.zuosh.rpc.protocol.Invoker;
import com.zuosh.rpc.protocol.ServerInvoker;

import java.util.Map;

public class ZkDirectory implements NotifyListener {
    private static Map<String, Invoker> invokersMap = Maps.newConcurrentMap();


    @Override
    public void notify(ServiceUrl serviceUrl, NotifyEvent event) {
        //
        ZConstants.LOGGER.info(" 获取到服务目录注册通知, 地址 :{}, 事件:{}", serviceUrl.getPath(), event.name());
        switch (event) {
            case add:
                ZConstants.LOGGER.info("目录服务收到 添加服务 事件,path: {}", serviceUrl.getPath());
                addInvokers(serviceUrl);
                break;
            case remove:
                ZConstants.LOGGER.warn("目录服务收到 删除服务 事件,path:{}", serviceUrl.getPath());
                invokersMap.remove(serviceUrl.getPath());
                break;
            default:
                break;

        }
        //
    }

    /**
     * 添加到目录服务
     *
     * @param serviceUrl
     */
    private void addInvokers(ServiceUrl serviceUrl) {
        Invocation invocation = new Invocation();
        invocation.setHost(ZConstants.PROVIDER_HOST);
        invocation.setPort(ZConstants.PROVIDER_PORT);
        invocation.setServiceUrl(serviceUrl);
        //
        String name = serviceUrl.getPath();
        //
//        invokersMap.putIfAbsent(name, new DefaultProtocol().refer(serviceUrl.getClass(), serviceUrl));
        invokersMap.putIfAbsent(name, new ServerInvoker(null));
    }

    /**
     * 拿到所有的invoker
     *
     * @return
     */
//    public List<Invoker> list() {
//        List<Invoker> invokers = Lists.newArrayList();
//        for (Map.Entry<String, Invoker> invokerEntry : invokersMap.entrySet()) {
//            invokers.add(invokerEntry.getValue());
//        }
//        return invokers;
//    }

    /**
     * 获取单节点 invoker
     *
     * @param url
     * @return
     */
    public Invoker getInvoker(ServiceUrl url) {
        String key = url.getPath();
        return invokersMap.get(key);
    }


    //

}
