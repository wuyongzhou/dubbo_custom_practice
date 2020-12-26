package com.study.dubbo.config.util;

import com.study.dubbo.common.tools.SpiUtils;
import com.study.dubbo.config.ProtocolConfig;
import com.study.dubbo.config.ServiceConfig;
import com.study.dubbo.rpc.Invoker;
import com.study.dubbo.rpc.protocol.Protocol;
import com.study.dubbo.rpc.proxy.ProxyFactory;

import java.net.NetworkInterface;
import java.net.URI;

/**
 * 作为自定义协议的最上级调用者，与Spring环境做相互交接，用于暴露发布一个网络服务。
 */
public class WprcBootstrap {

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
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
