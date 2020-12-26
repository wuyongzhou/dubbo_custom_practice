package com.study.dubbo.remoting.netty;

import com.study.dubbo.remoting.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.List;

/**
 * 对Netty网络框架接收到的信息进行编、解码，得到具体的java对象信息
 * 需要注意网络底层中常见的沾包和拆包现象
 */
public class NettyCodec extends ChannelDuplexHandler {
    private Codec codec;

    public NettyCodec(Codec codec) {
        this.codec=codec;
    }

    /**
     * 解码操作，对自定义协议进行解析，得到RpcInvocation对象交给下一个handler处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //1.获取数据
        ByteBuf byteBuf= (ByteBuf) msg;
        byte[] bytes=new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        //2.进行具体的解码操作，由具体的协议执行
        List<Object> objects = codec.decode(bytes);

        //这里有可能返回空的集合或者N个对象集合，具体根据网络是否沾、拆包
        for (Object object : objects) {
            //交给下个handler处理
            ctx.fireChannelRead(object);
        }

    }

    /**
     * 编码，把byte数组数据转为ByteBuf返回响应
     * @param ctx
     * @param msg
     * @param promise
     * @throws Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        byte[] bytes = codec.encode(msg);
        super.write(ctx, Unpooled.wrappedBuffer(bytes), promise);
    }
}
