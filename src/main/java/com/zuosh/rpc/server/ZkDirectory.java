package com.zuosh.rpc.server;

import com.google.common.collect.Maps;
import com.zuosh.rpc.common.ZConstants;
import com.zuosh.rpc.protocol.DefaultProtocol;
import com.zuosh.rpc.protocol.Invocation;
import com.zuosh.rpc.protocol.Invoker;

import java.util.Map;

public class ZkDirectory implements NotifyListener {
    private static Map<String, Invoker> invokersMap = Maps.newConcurrentMap();


    @Override
    public void notify(ServiceUrl serviceUrl) {
        //
        ZConstants.LOGGER.info(" 获取到服务目录注册通知, 地址 :{}", serviceUrl.getClass().getName());
        Invocation invocation = new Invocation();
        invocation.setHost(ZConstants.PROVIDER_HOST);
        invocation.setPort(ZConstants.PROVIDER_PORT);
        invocation.setServiceUrl(serviceUrl);
        //
        String name = serviceUrl.getPath();
        //
        invokersMap.putIfAbsent(name, new DefaultProtocol().refer(serviceUrl.getClass(), serviceUrl));
        //
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
