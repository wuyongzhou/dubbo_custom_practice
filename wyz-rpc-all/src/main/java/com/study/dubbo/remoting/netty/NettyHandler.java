package com.study.dubbo.remoting.netty;

import com.study.dubbo.remoting.Handler;
import com.study.dubbo.remoting.WrpcChannel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * 用于处理接收的远程调用请求以及返回信息的处理
 */
public class NettyHandler extends ChannelDuplexHandler {

    private Handler handler;

    public NettyHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * 接收
     * @param ctx 这里是Netty的channel对象，会经过包装为自定义协议的channel
     * @param msg 接收解码后的数据，也就是RpcInvocation对象
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        handler.onReceive(new NettyChannel(ctx.channel()),msg);
    }

    /**
     * 返回
     * @param ctx
     * @param msg
     * @param promise
     * @throws Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }
}
