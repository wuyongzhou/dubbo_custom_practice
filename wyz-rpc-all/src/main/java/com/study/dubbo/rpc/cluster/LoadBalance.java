package com.study.dubbo.rpc.cluster;

import com.study.dubbo.rpc.Invoker;

import java.net.URI;
import java.util.Map;

public interface LoadBalance {

    Invoker balance(Map<URI,Invoker> invokerMap);
}
