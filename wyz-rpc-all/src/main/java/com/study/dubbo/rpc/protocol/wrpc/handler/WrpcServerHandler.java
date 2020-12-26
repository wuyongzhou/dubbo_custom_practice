package com.study.dubbo.rpc.protocol.wrpc.handler;

import com.study.dubbo.common.serialize.Serialization;
import com.study.dubbo.remoting.Handler;
import com.study.dubbo.remoting.WrpcChannel;
import com.study.dubbo.rpc.Invoker;
import com.study.dubbo.rpc.Response;
import com.study.dubbo.rpc.RpcInvocation;

import java.net.URI;

public class WrpcServerHandler implements Handler {

    private Invoker invoker;
    private Serialization serialization;

    public WrpcServerHandler(Invoker invoker,Serialization serialization) {
        this.invoker = invoker;
        this.serialization=serialization;
    }

    @Override
    public void onReceive(WrpcChannel wrpcChannel, Object message) throws Exception {
        Response response=new Response();
        RpcInvocation rpcInvocation= (RpcInvocation) message;
        System.out.println("收到rpcInvocation信息："+rpcInvocation);
        //执行客户端请求，得到结果后，对客户端请求做出响应
        try {
            Object result = invoker.invoke(rpcInvocation);
            System.out.println("服务端执行结果："+result);
            response.setStatus(200);
            response.setContent(result);
        }catch (Throwable throwable){
            throwable.printStackTrace();
            response.setStatus(99);
            response.setContent(throwable.getMessage());
        }
        //发送结果数据
        byte[] bodyBytes = serialization.serialize(response);
        //wrpcChannel.send(bodyBytes);
    }

    @Override
    public void onWrite(WrpcChannel wrpcChannel, Object message) throws Exception {

    }
}
