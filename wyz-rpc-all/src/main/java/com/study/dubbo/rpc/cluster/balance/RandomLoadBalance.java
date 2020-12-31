package com.study.dubbo.rpc.cluster.balance;

import com.study.dubbo.rpc.Invoker;
import com.study.dubbo.rpc.cluster.LoadBalance;

import java.net.URI;
import java.util.Map;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {
    @Override
    public Invoker balance(Map<URI, Invoker> invokerMap) {
        int nextInt = new Random().nextInt(invokerMap.size());
        return invokerMap.values().toArray(new Invoker[]{})[nextInt];
    }
}
