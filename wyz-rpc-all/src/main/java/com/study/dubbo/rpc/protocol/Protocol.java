package com.study.dubbo.rpc.protocol;

import com.study.dubbo.rpc.Invoker;

import java.net.URI;

public interface Protocol {

    /**
     * 开放服务
     * @param exportUri 协议名称://IP:端口/service全类名?参数名称=参数值&参数1名称=参数2值
     * @param invoker 调用具体实现类的代理对象
     */
    void export(URI exportUri, Invoker invoker);
}
