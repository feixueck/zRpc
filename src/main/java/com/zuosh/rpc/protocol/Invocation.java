package com.zuosh.rpc.protocol;

import com.zuosh.rpc.server.ServiceUrl;

public class Invocation {
    private String serviceName;//服务名
    private String host;//主机地址
    private int port;
    private String params;//相关参数
    //
    private ServiceUrl serviceUrl;


    public ServiceUrl getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(ServiceUrl serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
