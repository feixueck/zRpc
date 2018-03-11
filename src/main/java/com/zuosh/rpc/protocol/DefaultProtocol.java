package com.zuosh.rpc.protocol;

import com.google.common.collect.Maps;
import com.zuosh.rpc.common.ZConstants;
import com.zuosh.rpc.netty.*;
import com.zuosh.rpc.server.ServiceUrl;

import java.util.Map;

public class DefaultProtocol implements Protocol {
    private Map<String, Invoker> invokerMap = Maps.newConcurrentMap();


    @Override
    public <T> Protocol export(String interfaceName, Class<T> tClass) {
        //注册服务逻辑
        //还有不包含 ,才会去注册 ,但是要防止重复注册 多线程问题
        if (!invokerMap.containsKey(interfaceName)) {
            Invoker invoker = new ServerInvoker(tClass);
            invokerMap.putIfAbsent(interfaceName, invoker);
        }
        return this;
    }

    /**
     * 获取方法执行体,invoker
     *
     * @param serviceName
     * @return
     */
    private Invoker getInvoker(String serviceName) {
        //
        return invokerMap.get(serviceName);
    }

    /**
     * 获取 invoker
     *
     * @param tClass
     * @param url
     * @param <T>
     * @return
     */
    @Override
    public <T> Invoker refer(Class<T> tClass, ServiceUrl<T> url) {
        NettyClient client = new NettyClient(consumerRpcHandler);
        NettyChannel nettyChannel = client.connect(ZConstants.PROVIDER_HOST, ZConstants.PROVIDER_PORT);
//        nettyChannel.send(new Request());
        // get invoker
        Invoker invoker = new Invoker() {
            @Override
            public Object invoke(Invocation invocation) throws InterruptedException {
                //build request
                Request request = Request.buildStandardRequest(invocation, "oh");
                //
                ResponseFuture future = new ResponseFuture(request.getMsgId());
                //send
                nettyChannel.send(request);
                //wait response
                Response response = future.get();//will wait
                ZConstants.LOGGER.info(" get response from provider : " + response.getResult());
                return response.getResult();
            }
        };
        return invoker;
    }

    /**
     * 启动服务端
     */
    public void openServer() {
        NettyServer nettyServer = new NettyServer(providerRpcHandler);
        //
        nettyServer.bind(ZConstants.PROVIDER_PORT);
        //
    }

    //消费端 获取服务结果
    private final RpcHandler consumerRpcHandler = new AbstractRpcHandler() {
        @Override
        public void receive(NettyChannel channel, Object object) {
            if (object instanceof Response) {
                Response response = (Response) object;
                ResponseFuture future = ResponseFuture.getFuture(response.getMsgId());
                future.receive(response);
            }
        }
    };
    //服务端进行服务调用
    private final RpcHandler providerRpcHandler = new AbstractRpcHandler() {
        @Override
        public void receive(NettyChannel channel, Object object) {
            //consumer收到回复
            ZConstants.LOGGER.info(" rpcHandler recv msg: {}", object.getClass());
            if (object instanceof Request) {
                Request request = (Request) object;
                //
                Invocation invocation = request.getInvocation();
                //
                Invoker invoker = getInvoker(invocation.getServiceName());
                //
                try {
                    Object invoke = invoker.invoke(invocation);
                    //
                    Response response = Response.buildResponse(request.getMsgId(), invoke, "ok");
                    //send response
                    channel.send(response);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
