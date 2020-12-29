package com.study.dubbo.remoting.netty;

import com.study.dubbo.remoting.Client;
import com.study.dubbo.remoting.Codec;
import com.study.dubbo.remoting.Handler;
import com.study.dubbo.remoting.WrpcChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.URI;

public class NettyClient implements Client {

    private EventLoopGroup eventLoopGroup;
    private WrpcChannel wrpcChannel;

    @Override
    public void connect(URI uri, Codec codec, Handler handler) {
        try {
            //标准的Netty客户端启动逻辑
            eventLoopGroup=new NioEventLoopGroup();
            Bootstrap bootstrap=new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new NettyCodec(codec.createInstance()));
                            pipeline.addLast(new NettyHandler(handler));
                        }
                    });
            ChannelFuture future = bootstrap.connect(uri.getHost(), uri.getPort()).sync();
            wrpcChannel=new NettyChannel(future.channel());
            System.out.println("成功连接到提供者服务端......");

            //优雅停机 ---- 响应 kill 命令
            Runtime.getRuntime().addShutdownHook(new Thread(() ->{
                try {
                    System.out.println("马上要停机了啊.........");
                    synchronized (NettyServer.class){
                        eventLoopGroup.shutdownGracefully();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public WrpcChannel getChannel() {
        return wrpcChannel;
    }
}
