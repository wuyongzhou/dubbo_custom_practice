package com.study.dubbo.registry;

import java.net.URI;
import java.util.Set;

/**
 * 订阅服务时的监听器，用于响应关注的服务发出的事件
 */
public interface NotifyListener {
    void notify(Set<URI> uris);
}
