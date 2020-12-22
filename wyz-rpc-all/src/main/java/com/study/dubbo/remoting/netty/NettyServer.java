package com.study.dubbo.remoting.netty;

import com.study.dubbo.remoting.Codec;
import com.study.dubbo.remoting.Handler;
import com.study.dubbo.remoting.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.net.URI;

public class NettyServer implements Server {
    EventLoopGroup boss = new NioEventLoopGroup();
    EventLoopGroup worker = new NioEventLoopGroup();

    @Override
    public void start(URI uri, Codec codec, Handler handler) {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    //指定所使用的nio传输channel
                    .channel(NioServerSocketChannel.class)
                    //指定要监听的地址
                    .localAddress(new InetSocketAddress(uri.getHost(), uri.getPort()))
                    //添加pipeline责任链handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel){
                            //处理网络信息编、解码
                            socketChannel.pipeline().addLast(new NettyCodec(codec));
                            //处理远程服务调用
                            socketChannel.pipeline().addLast(new NettyHandler(handler));
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync();
            System.out.println("完成端口绑定和服务器启动");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
