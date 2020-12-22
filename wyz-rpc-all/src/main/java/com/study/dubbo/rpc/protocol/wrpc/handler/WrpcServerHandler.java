package com.study.dubbo.rpc.protocol.wrpc.handler;

import com.study.dubbo.remoting.Handler;
import com.study.dubbo.remoting.WrpcChannel;
import com.study.dubbo.rpc.RpcInvocation;

public class WrpcServerHandler implements Handler {
    @Override
    public void onReceive(WrpcChannel wrpcChannel, Object message) throws Exception {
        RpcInvocation rpcInvocation= (RpcInvocation) message;
        System.out.println("收到rpcInvocation信息："+rpcInvocation);
    }

    @Override
    public void onWrite(WrpcChannel wrpcChannel, Object message) throws Exception {

    }
}
