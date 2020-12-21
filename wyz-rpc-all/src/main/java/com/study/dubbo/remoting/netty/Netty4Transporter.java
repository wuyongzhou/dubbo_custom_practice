package com.study.dubbo.remoting.netty;

import com.study.dubbo.remoting.Server;
import com.study.dubbo.remoting.Transporter;

import java.net.URI;

public class Netty4Transporter implements Transporter {
    @Override
    public Server start(URI uri) {
        return null;
    }

    @Override
    public void connect(URI uri) {

    }
}
