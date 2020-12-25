package com.study.dubbo.rpc;

/**
 * 可以理解为桥梁，不论是服务提供者还是消费者都是通过Invoker调用
 *
 */
public interface Invoker {

    /**
     * 返回接口
     * @return
     */
    Object getInterface();


    /**
     * 发起调用【负载均衡、容错、重连..等等都包含在内】
     * @param rpcInvocation
     * @return
     * @throws Exception
     */
    Object invoke(RpcInvocation rpcInvocation)throws Exception;
}
