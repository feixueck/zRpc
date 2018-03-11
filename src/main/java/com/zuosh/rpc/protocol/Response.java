package com.zuosh.rpc.protocol;

import java.io.Serializable;

public class Response implements Serializable {
    private Object result;
    private long msgId;
    private String msg;

    //
    public static Response buildResponse(Long msgId, Object object, String msg) {
        //
        Response response = new Response();
        response.setResult(object);
        response.setMsg(msg);
        response.setMsgId(msgId);
        return response;
    }

    public static Response buildNullResponse() {
        Response response = new Response();
        response.setMsg("no response");
        return response;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }
}
