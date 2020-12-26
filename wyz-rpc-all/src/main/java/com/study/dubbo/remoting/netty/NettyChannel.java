package com.study.dubbo.remoting.netty;

import com.study.dubbo.remoting.WrpcChannel;
import io.netty.channel.Channel;

public class NettyChannel implements WrpcChannel {

    private Channel channel;

    public NettyChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void send(byte[] message) {
        channel.writeAndFlush(message);
    }
}
