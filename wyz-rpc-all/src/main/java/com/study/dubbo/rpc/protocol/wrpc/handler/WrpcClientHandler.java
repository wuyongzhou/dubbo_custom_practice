package com.study.dubbo.rpc.protocol.wrpc.handler;

import com.study.dubbo.remoting.Handler;
import com.study.dubbo.remoting.WrpcChannel;
import com.study.dubbo.rpc.Response;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 服务消费者真正执行业务处理的类
 */
public class WrpcClientHandler implements Handler {

    private static final Map<Long, CompletableFuture> invokerMap=new ConcurrentHashMap();

    /**
     * 通过创建future来让请求线程异步获知响应结果的到来
     * @param messageId 关联id，在请求发送时自动生成，响应结果中会带有请求的id，证明它们是一对【请求-响应】的结果
     * @return
     */
    public static CompletableFuture waitResult(long messageId){
        CompletableFuture completableFuture=new CompletableFuture();
        invokerMap.put(messageId,completableFuture);
        return completableFuture;
    }

    /**
     * 收到服务端响应
     * 这里收到响应结果的线程是网络框架的线程，需要通过future异步通知机制来让发起请求的线程能动态感知到结果已收到
     * @param wrpcChannel
     * @param message
     * @throws Exception
     */
    @Override
    public void onReceive(WrpcChannel wrpcChannel, Object message) throws Exception {
        //1.得到的必然是一个Response对象，这是在解码时通过decodeType决定的
        Response response=(Response) message;
        //2.把Response对象放入future中，根据id进行绑定关联
        invokerMap.get(response.getRequestId()).complete(response);
    }

    /**
     * 发送远程调用请求
     * @param wrpcChannel
     * @param message
     * @throws Exception
     */
    @Override
    public void onWrite(WrpcChannel wrpcChannel, Object message) throws Exception {
        wrpcChannel.send((byte[]) message);
    }
}
