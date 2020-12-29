package com.study.dubbo.rpc.protocol.wrpc.handler;

import com.study.dubbo.remoting.Handler;
import com.study.dubbo.remoting.WrpcChannel;

/**
 * 服务消费者真正执行业务处理的类
 */
public class WrpcClientHandler implements Handler {
    /**
     * 收到服务端响应
     * @param wrpcChannel
     * @param message
     * @throws Exception
     */
    @Override
    public void onReceive(WrpcChannel wrpcChannel, Object message) throws Exception {

    }

    /**
     * 发送远程调用请求
     * @param wrpcChannel
     * @param message
     * @throws Exception
     */
    @Override
    public void onWrite(WrpcChannel wrpcChannel, Object message) throws Exception {

    }
}
