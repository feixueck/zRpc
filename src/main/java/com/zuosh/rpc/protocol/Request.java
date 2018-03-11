package com.zuosh.rpc.protocol;

import com.zuosh.rpc.common.ZConstants;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class Request implements Serializable {
    private long msgId;
    private Invocation invocation;
    public static AtomicLong autoGen = new AtomicLong(0);
    private String msg;
    //

    public Request() {
        this.msgId = Request.autoGen.incrementAndGet();
    }

    /**
     * 构造一个简单的sample
     *
     * @param msg
     * @return
     */
    public static Request buildRequest(String msg) {
        Request request = new Request();
        request.setMsg(msg);
        return request;
    }

    /**
     * 构造标准请求
     *
     * @param invocation
     * @param msg
     * @return
     */
    public static Request buildStandardRequest(Invocation invocation, String msg) {
        Request request = new Request();
        request.setMsg(msg);
        request.setInvocation(invocation);
        ZConstants.LOGGER.info(" Request build ok ,id: {}", request.getMsgId());
        return request;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
