package com.zuosh.rpc.protocol;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        //
        Invoker invoker = new Invoker() {
            @Override
            public Object invoke(Invocation invocation) throws InterruptedException {
                Thread.sleep(3000);
                System.out.println("this is test");
                System.out.println(invocation.getHost() + " ==>" + invocation.getPort());
                return invocation;
            }
        };

        //
        Thread.sleep(1000);
        //
        Invocation invocation = new Invocation();
        invocation.setHost("localhost");
        invocation.setPort(8888);
        invoker.invoke(invocation);
    }
}
