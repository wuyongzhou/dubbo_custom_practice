package com.study.dubbo.remoting.netty;

import com.study.dubbo.remoting.Handler;
import com.study.dubbo.remoting.WrpcChannel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 用于处理接收的远程调用请求
 */
public class NettyHandler extends ChannelDuplexHandler {

    private Handler handler;

    public NettyHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //System.out.println("收到消息："+msg);
        handler.onReceive(new NettyChannel(ctx.channel()),msg);
    }
}
