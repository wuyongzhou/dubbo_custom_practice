package com.study.dubbo.remoting.netty;

import com.study.dubbo.remoting.*;

import java.net.URI;

public class Netty4Transporter implements Transporter {
    @Override
    public Server start(URI uri, Codec codec, Handler handler) {
        Server server=new NettyServer();
        server.start(uri,codec,handler);
        return server;
    }

    @Override
    public Client connect(URI uri,Codec codec, Handler handler) {
        Client client=new NettyClient();
        client.connect(uri,codec,handler);
        return client;
    }
}
