package com.zuosh.rpc.server;

import com.zuosh.rpc.common.ZConstants;
import org.apache.curator.utils.ZKPaths;

public class ServiceUrl<T> {
    //
    private Class<T> aClass;
    //
    private String methodName;

    //
    private Class<?>[] parameters;

    //
    public String getPath() {
        //
        return ZKPaths.makePath(ZConstants.RPC_ROOT_PATH, aClass.getName());
    }

    public Class<T> getaClass() {
        return aClass;
    }

    public void setaClass(Class<T> aClass) {
        this.aClass = aClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameters() {
        return parameters;
    }

    public void setParameters(Class<?>[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public int hashCode() {
        return this.getPath().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ServiceUrl) {
            //
            ServiceUrl remote = (ServiceUrl) obj;
            return this.getPath().equals(remote.getPath());
        } else {
            return super.equals(obj);
        }
    }
}
