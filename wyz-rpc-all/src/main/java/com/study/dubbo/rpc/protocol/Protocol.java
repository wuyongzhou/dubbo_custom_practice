package com.study.dubbo.rpc.protocol;

import java.net.URI;

public interface Protocol {

    void export(URI exportUri);
}
