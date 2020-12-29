package com.study.dubbo.config.util;

import com.study.dubbo.common.serialize.Serialization;
import com.study.dubbo.common.tools.ByteUtil;
import com.study.dubbo.common.tools.SpiUtils;
import com.study.dubbo.config.ProtocolConfig;
import com.study.dubbo.config.ReferenceConfig;
import com.study.dubbo.config.RegistryConfig;
import com.study.dubbo.config.ServiceConfig;
import com.study.dubbo.registry.Registry;
import com.study.dubbo.rpc.Invoker;
import com.study.dubbo.rpc.RpcInvocation;
import com.study.dubbo.rpc.protocol.Protocol;
import com.study.dubbo.rpc.proxy.ProxyFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 作为自定义协议的最上级调用者，与Spring环境做相互交接.
 * 对于提供者：用于暴露发布一个网络服务。
 * 对于消费者：用于获得一个代理对象,作为实现类。
 */
public class WprcBootstrap {

    public static Object getReferenceBean(ReferenceConfig referenceConfig){
        Object referenceBean=null;
        try {
            //通过protocol启动Netty客户端连接到服务端并获得invoker对象

            //这里的invoker对象其实是被代理对象
            Invoker invoker=null;
            /**
             * 创建返回的代理对象实现了使用 @WRpcReference 注解修饰属性的接口，这样子从外部看来就是该接口的实现类，可以注入其中。
             * 但其作用只是拼装一些远程调用所需的参数，真正执行是通过invoker对象发起远程调用。
             */
            referenceBean=ProxyFactory.getProxy(invoker,new Class[]{referenceConfig.getService()});
        }catch (Exception e){
            e.printStackTrace();
        }
        return referenceBean;
    }

    public static void export(ServiceConfig serviceConfig){
        try {
            //遍历protocol集合，因为有可能存在一个服务多种不同的协议同时支持，这就需要对外暴露多种不同协议的网络服务
            for (ProtocolConfig protocolConfig:serviceConfig.getProtocolConfigs()){
                //1. 定义URI
                //sample: 协议名称://IP:端口/service全类名?参数名称=参数值&参数1名称=参数2值
                StringBuilder stringBuilder=new StringBuilder();
                //协议名称
                stringBuilder.append(protocolConfig.getName()+"://");
                //IP
                String hostAddress = NetworkInterface.getNetworkInterfaces().nextElement().getInterfaceAddresses().get(0).getAddress().getHostAddress();
                stringBuilder.append(hostAddress+":");
                //端口
                stringBuilder.append(protocolConfig.getPort()+"/");
                //service全类名
                //【 照目前看来，这个属性并不影响客户端调用，主要还是根据 IP+端口 以及自定义协议头，就可以调用到具体服务 】
                stringBuilder.append(serviceConfig.getService().getName()+"?");
                //参数
                stringBuilder.append("transporter="+protocolConfig.getTransporter());
                stringBuilder.append("&serialization="+protocolConfig.getSerialization());
                URI exportUri=new URI(stringBuilder.toString());
                System.out.println("exportUri："+exportUri);

                //2. 通过ProxyFactory获取invoker实现类
                Invoker invoker = ProxyFactory.getInvoker(serviceConfig.getReference(), serviceConfig.getService());

                Protocol protocol = SpiUtils.getServiceImpl(protocolConfig.getName(), Protocol.class);

                //3. 真正的暴露发布服务
                System.out.println("开始暴露网络服务.....");
                protocol.export(exportUri,invoker);

                //4. 注册服务到注册中心，要考虑会有多个的情况，但是并不需要
                System.out.println("开始把服务注册到注册中心");
                for (RegistryConfig registryConfig : serviceConfig.getRegistryConfigs()) {
                    URI registryUri=new URI(registryConfig.getAddress());
                    Registry registry = SpiUtils.getServiceImpl(registryUri.getScheme(), Registry.class);
                    registry.init(registryUri);
                    registry.registerService(exportUri);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
