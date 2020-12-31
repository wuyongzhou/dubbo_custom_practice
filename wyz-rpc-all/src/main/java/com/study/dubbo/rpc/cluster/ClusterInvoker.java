package com.study.dubbo.rpc.cluster;

import com.study.dubbo.common.tools.SpiUtils;
import com.study.dubbo.common.tools.URIUtils;
import com.study.dubbo.config.ReferenceConfig;
import com.study.dubbo.config.RegistryConfig;
import com.study.dubbo.registry.Registry;
import com.study.dubbo.rpc.Invoker;
import com.study.dubbo.rpc.RpcInvocation;
import com.study.dubbo.rpc.cluster.balance.RandomLoadBalance;
import com.study.dubbo.rpc.protocol.Protocol;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多服务提供者实例时所使用的Invoker
 */
public class ClusterInvoker implements Invoker {

    private ReferenceConfig referenceConfig;
    private LoadBalance loadBalance;
    //保存服务实例
    private Map<URI,Invoker> invokerMap=new ConcurrentHashMap<>();

    public ClusterInvoker(ReferenceConfig referenceConfig) throws URISyntaxException {
        this.referenceConfig=referenceConfig;
        this.loadBalance = SpiUtils.getServiceImpl(referenceConfig.getLoadbalance(), LoadBalance.class);
        for (RegistryConfig registryConfig:referenceConfig.getRegistryConfigs()){
            URI registryUri=new URI(registryConfig.getAddress());
            Registry registry = SpiUtils.getServiceImpl(registryUri.getScheme(), Registry.class);
            registry.init(registryUri);
            registry.subscribeService(referenceConfig.getService().getName(),(uris)->{
                System.out.println("更新前的服务实例信息---------："+invokerMap);
                /**
                 * uris为N个，协议、ip、端口会有差异，但肯定是同一个服务，例子如下：
                 * WrpcProtocol://127.0.0.1:10088/com.study.dubbo.sms.api.SmsService?transporter=Netty4Transporter&serialization=JsonSerialization
                 * WrpcProtocol://127.0.0.1:10087/com.study.dubbo.sms.api.SmsService?transporter=Netty4Transporter&serialization=JsonSerialization
                 * 每循环一次代表有一个服务实例
                 */

                //由于有可能会存在服务实例变更的情况，客户端也要动态调整

                //剔除
                for (URI uri:invokerMap.keySet()){
                    //客户端有，注册中心没有
                    if(!uris.contains(uri)){
                        invokerMap.remove(uri);
                    }
                }

                //新增
                for (URI uri:uris){
                    //客户端没有，而注册中心有
                    if(!invokerMap.containsKey(uri)){
                        //根据服务实例在注册中心登记的uri信息，生成对应的protocol
                        Protocol protocol = SpiUtils.getServiceImpl(uri.getScheme(), Protocol.class);
                        //这里的invoker代表客户端长连接
                        Invoker invoker = protocol.refer(uri);
                        invokerMap.putIfAbsent(uri,invoker);
                    }
                }
                System.out.println("更新后的服务实例信息---------："+invokerMap);
            });
        }
    }

    @Override
    public Object getInterface() {
        return referenceConfig.getService();
    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) throws Exception {
        //注解中属性指定+SPI机制
        Invoker invoker = loadBalance.balance(invokerMap);
        return invoker.invoke(rpcInvocation);
    }
}
