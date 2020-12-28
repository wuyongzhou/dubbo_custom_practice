package com.study.dubbo.rpc.proxy;

import com.study.dubbo.rpc.Invoker;
import com.study.dubbo.rpc.RpcInvocation;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFactory {

    /**
     * 获取指定接口的实现类，但该类的实现方法由 invoker 具体执行
     * @param invoker
     * @param interfaces
     * @return
     */
    public static Object getProxy(Invoker invoker,Class<?>[] interfaces){
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),interfaces,new InvokerInvocationHandler(invoker));
    }

    /**
     * 获取实现类的代理对象，封装在实现类之上。
     * @param proxy
     * @param type
     * @return
     */
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
