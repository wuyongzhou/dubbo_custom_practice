package com.study.dubbo.rpc.protocol.wrpc.handler;

import com.study.dubbo.remoting.Handler;
import com.study.dubbo.remoting.WrpcChannel;
import com.study.dubbo.rpc.Invoker;
import com.study.dubbo.rpc.RpcInvocation;

import java.net.URI;

public class WrpcServerHandler implements Handler {

    private Invoker invoker;

    public WrpcServerHandler(Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public void onReceive(WrpcChannel wrpcChannel, Object message) throws Exception {
        RpcInvocation rpcInvocation= (RpcInvocation) message;
        System.out.println("收到rpcInvocation信息："+rpcInvocation);
    }

    @Override
    public void onWrite(WrpcChannel wrpcChannel, Object message) throws Exception {

    }
}
