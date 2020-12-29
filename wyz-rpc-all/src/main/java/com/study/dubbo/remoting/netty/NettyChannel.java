package com.study.dubbo.remoting.netty;

import com.study.dubbo.remoting.WrpcChannel;
import io.netty.channel.Channel;

/**
 * 虽然叫NettyChannel，但是并不是Netty包下的对象，而是自定义协议中的channel实现类
 */
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
