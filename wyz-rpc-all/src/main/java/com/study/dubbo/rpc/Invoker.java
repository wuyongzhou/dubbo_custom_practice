package com.study.dubbo.rpc;

/**
 * 静态代理接口，定义行为规范
 * 服务提供者和服务消费者都是通过该接口执行具体操作
 */
public interface Invoker {

    /**
     * 返回接口.
     */
    Class getInterface();

    /**
     * 发起调用【负载均衡、容错、重连..都在这里面了】
     *
     * @param rpcInvocation 调用所需的参数
     * @return 执行结果
     * @throws Exception
     */
    Object invoke(RpcInvocation rpcInvocation) throws Exception;
}
