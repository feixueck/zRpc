package com.zuosh.rpc.protocol;

import com.zuosh.rpc.server.ServiceUrl;

import java.io.Serializable;

public class Invocation implements Serializable {
    private String serviceName;//服务名
    private String host;//主机地址
    private int port;
    private String params;//相关参数
    //=====下面参数是暂时能用得到的 ,上面的暂时备用
    private ServiceUrl serviceUrl;
    private Class<?>[] parameterTypes;
    private Class<?> clazz;
    private String methodName;//方法名
    private Object[] arguments;//参数

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

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
