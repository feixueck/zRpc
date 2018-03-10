package com.zuosh.rpc.server;

import com.google.common.collect.Maps;
import com.zuosh.rpc.common.ZConstants;
import com.zuosh.rpc.protocol.Invocation;

import java.util.Map;

public class ZkDirectory implements NotifyListener {
    private static Map<String, Invocation> invokersMap = Maps.newConcurrentMap();


    @Override
    public void notify(ServiceUrl serviceUrl) {
        //
        ZConstants.LOGGER.info(" 获取到服务目录注册通知, 地址 :{}", serviceUrl.getClass().getName());
        Invocation invocation = new Invocation();
        invocation.setHost(ZConstants.PROVIDER_HOST);
        invocation.setPort(ZConstants.PROVIDER_PORT);
        invocation.setServiceUrl(serviceUrl);
        //
        String name = serviceUrl.getaClass().getName();
        //
        invokersMap.putIfAbsent(name, invocation);
        //
    }



    //

}
