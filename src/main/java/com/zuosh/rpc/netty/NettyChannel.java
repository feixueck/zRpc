package com.zuosh.rpc.netty;

import com.google.common.collect.Maps;
import com.zuosh.rpc.common.ZConstants;
import io.netty.channel.Channel;

import java.util.Map;

public class NettyChannel {
    //prover  和 consumer  通用一个
    public static Map<Channel, NettyChannel> channelsMap = Maps.newHashMap();
    private Channel channel;

    public NettyChannel(Channel channel) {
        this.channel = channel;
    }

    //
    public static void addChannel(Channel channel) {
        ZConstants.LOGGER.info(" 监控到 channel加入 :{}", channel.metadata());
        NettyChannel nettyChannel = new NettyChannel(channel);
        channelsMap.putIfAbsent(channel, nettyChannel);
    }

    public static NettyChannel getChannel(Channel channel) {
        return channelsMap.get(channel);
    }

    //
    public static void removeChannel(Channel channel) {
        channelsMap.remove(channel);
    }

    //
    public static void send(Channel channel, Object msg) {
        channel.writeAndFlush(msg);
    }

    //增加一个实例方法 ,发送消息
    public void send(Object msg) {
        channel.writeAndFlush(msg);
    }
}
