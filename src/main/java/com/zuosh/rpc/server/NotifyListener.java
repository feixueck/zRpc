package com.zuosh.rpc.server;

public interface NotifyListener {
    //通知接口
    void notify(ServiceUrl serviceUrl);

//    //加监听
//    void addListener(ServiceUrl serviceUrl);
}
