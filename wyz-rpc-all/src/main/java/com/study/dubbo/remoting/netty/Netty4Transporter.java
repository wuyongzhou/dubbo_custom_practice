package com.study.dubbo.remoting.netty;

import com.study.dubbo.remoting.Server;
import com.study.dubbo.remoting.Transporter;

import java.net.URI;

public class Netty4Transporter implements Transporter {
    @Override
    public Server start(URI uri) {
        Server server=new NettyServer();
        server.start(uri);
        return server;
    }

    @Override
    public void connect(URI uri) {

    }
}
