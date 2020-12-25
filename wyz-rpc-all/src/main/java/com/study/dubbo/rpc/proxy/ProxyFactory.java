package com.study.dubbo.rpc.proxy;

import com.study.dubbo.rpc.Invoker;
import com.study.dubbo.rpc.RpcInvocation;

import java.lang.reflect.Method;

public class ProxyFactory {

    public static Invoker getInvoker(Object proxy,Class type){
        return new Invoker() {
            @Override
            public Class getInterface() {
                return type;
            }

            @Override
            public Object invoke(RpcInvocation rpcInvocation) throws Exception {
                Method method = proxy.getClass().getMethod(rpcInvocation.getMethodName(), rpcInvocation.getParameterTypes());
                return method.invoke(proxy,rpcInvocation.getArguments());
            }
        };
    }
}
