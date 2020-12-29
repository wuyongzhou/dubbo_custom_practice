package com.study.dubbo.remoting;

import java.net.URI;

/**
 * 底层网络传输入口
 * 负责定义行为，但不包含具体实现
 */
public interface Transporter {
    Server start(URI uri, Codec codec, Handler handler);

    Client connect(URI uri,Codec codec, Handler handler);
}
