package com.study.dubbo.registry;

import java.net.URI;

public interface Registry {

    /**
     *  注册服务
     * @param exportUri
     */
    void registerService(URI exportUri);

    /**
     * 订阅服务
     */
    void subscribeService(String serviceName,NotifyListener notifyListener);

    /**
     * 初始化注册中心
     */
    void init(URI address);
}
