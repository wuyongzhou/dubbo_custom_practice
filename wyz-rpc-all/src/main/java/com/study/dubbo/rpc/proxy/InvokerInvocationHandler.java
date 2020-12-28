package com.study.dubbo.rpc.proxy;

import com.study.dubbo.rpc.Invoker;
import com.study.dubbo.rpc.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 定义代理对象的执行逻辑
 */
public class InvokerInvocationHandler implements InvocationHandler {

    private Invoker invoker;

    public InvokerInvocationHandler(Invoker invoker) {
        this.invoker = invoker;
    }

    /**
     * 并不执行具体业务逻辑，只是进行基本校验，本地方法直接执行调用，其余方法拼装PpcInvocation对象发起远程调用
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        // 不需要远程调用
        if (parameterTypes.length == 0) {
            if ("toString".equals(methodName)) {
                return invoker.toString();
            } else if ("$destroy".equals(methodName)) {
                return null;
            } else if ("hashCode".equals(methodName)) {
                return invoker.hashCode();
            }
        } else if (parameterTypes.length == 1 && "equals".equals(methodName)) {
            return invoker.equals(args[0]);
        }
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setMethodName(methodName);
        rpcInvocation.setArguments(args);
        rpcInvocation.setParameterTypes(parameterTypes);
        rpcInvocation.setServiceName(method.getDeclaringClass().getName());
        return invoker.invoke(rpcInvocation);
    }
}
