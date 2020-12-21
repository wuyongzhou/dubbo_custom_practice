package com.study.dubbo.remoting;

import java.net.URI;

public interface Transporter {
    Server start(URI uri);

    void connect(URI uri);
}
