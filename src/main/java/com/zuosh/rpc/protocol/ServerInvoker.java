package com.zuosh.rpc.protocol;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 服务端的invoker
 */
public class ServerInvoker implements Invoker {
    //真正的实现类
    private Class<?> clazz;

    public ServerInvoker(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Invocation invocation) throws InterruptedException {
        //
        String methodName = invocation.getMethodName();
        try {
            Object o = clazz.newInstance();
            //
            Method method = clazz.getMethod(methodName, invocation.getParameterTypes());
            //
            Object invoke = method.invoke(o, invocation.getArguments());
            return invoke;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
