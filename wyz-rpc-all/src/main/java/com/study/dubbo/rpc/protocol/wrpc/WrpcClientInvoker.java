package com.study.dubbo.rpc.protocol.wrpc;

import com.study.dubbo.common.serialize.Serialization;
import com.study.dubbo.remoting.Client;
import com.study.dubbo.rpc.Invoker;
import com.study.dubbo.rpc.Response;
import com.study.dubbo.rpc.RpcInvocation;
import com.study.dubbo.rpc.protocol.wrpc.handler.WrpcClientHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class WrpcClientInvoker implements Invoker {

    private Client client;
    private Serialization serialization;

    public WrpcClientInvoker(Client client, Serialization serialization) {
        this.client = client;
        this.serialization = serialization;
    }

    @Override
    public Object getInterface() {
        return null;
    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) throws Exception {
        //1.得到body序列化后的字节数组
        byte[] bytes = serialization.serialize(rpcInvocation);
        //2.发送数据，这里就代替了WrpcClientHandler对象的onWrite方法，相当于直接向网络框架发送数据
        client.getChannel().send(bytes);
        //3.得到响应，借助于future的异步通知特性
        CompletableFuture completableFuture = WrpcClientHandler.waitResult(rpcInvocation.getId());
        //如果没有具体的结果，这里会一直阻塞
        Object result = completableFuture.get(60, TimeUnit.SECONDS);
        Response response= (Response) result;
        //远程调用结果正常
        if(response.getStatus()==200){
            return response.getContent();
        }else{ //远程调用结果不正常
            throw new Exception("server error:"+response.getContent().toString());
        }
    }
}
