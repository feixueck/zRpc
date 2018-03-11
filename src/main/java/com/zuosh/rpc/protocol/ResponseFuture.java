package com.zuosh.rpc.protocol;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ResponseFuture {
    //
    private Response response;
    //
    private static Map<Long, ResponseFuture> futureMap = Maps.newConcurrentMap();
    //
    private Long msgId;
    //
    private Lock lock = new ReentrantLock();
    private Condition done = lock.newCondition();

    public ResponseFuture(Long msgId) {
        this.msgId = msgId;
        //add future map
        futureMap.putIfAbsent(msgId, this);
    }

    public static ResponseFuture getFuture(Long msgId) {
        return futureMap.get(msgId);
    }

    //
    public void receive(Response response) {
        lock.lock();
        try {
            this.response = response;
            done.signal();
        } finally {
            lock.unlock();
        }
    }

    private boolean isDone() {
        return response != null;
    }

    //
    public Response get() {
        if (isDone()) {
            return response;
        }
        //
        lock.lock();
        //
        while (!isDone()) {
            try {
                done.await(10, TimeUnit.SECONDS);
                if (isDone()) {
                    return response;
                } else {
                    return Response.buildNullResponse();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
        return Response.buildNullResponse();
    }
}
