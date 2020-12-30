package com.study.dubbo.rpc.cluster;

import com.study.dubbo.common.tools.SpiUtils;
import com.study.dubbo.config.ReferenceConfig;
import com.study.dubbo.config.RegistryConfig;
import com.study.dubbo.registry.Registry;
import com.study.dubbo.rpc.Invoker;
import com.study.dubbo.rpc.RpcInvocation;

import java.net.URI;
import java.net.URISyntaxException;

public class ClusterInvoker implements Invoker {

    public ClusterInvoker(ReferenceConfig referenceConfig) throws URISyntaxException {
        for (RegistryConfig registryConfig:referenceConfig.getRegistryConfigs()){
            URI registryUri=new URI(registryConfig.getAddress());
            Registry registry = SpiUtils.getServiceImpl(registryUri.getScheme(), Registry.class);
            registry.init(registryUri);
            registry.subscribeService(referenceConfig.getService().getName(),(uris)->{
                /**
                 * uris为N个，协议、ip、端口会有差异，但肯定是同一个服务，例子如下：
                 * WrpcProtocol://127.0.0.1:10088/com.study.dubbo.sms.api.SmsService?transporter=Netty4Transporter&serialization=JsonSerialization
                 * WrpcProtocol://127.0.0.1:10087/com.study.dubbo.sms.api.SmsService?transporter=Netty4Transporter&serialization=JsonSerialization
                 */

            });
        }
    }

    @Override
    public Object getInterface() {
        return null;
    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) throws Exception {
        return null;
    }
}
