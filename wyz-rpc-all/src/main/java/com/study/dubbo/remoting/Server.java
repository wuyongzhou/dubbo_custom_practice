package com.study.dubbo.remoting;

import java.net.URI;

/**
 * 网络服务的抽象接口，其实现类负责具体的网络服务相关处理
 */
public interface Server {

    void start(URI uri);
}
