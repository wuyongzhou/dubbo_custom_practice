package com.study.dubbo.rpc.protocol.wrpc;

import com.study.dubbo.common.serialize.Serialization;
import com.study.dubbo.common.tools.SpiUtils;
import com.study.dubbo.common.tools.URIUtils;
import com.study.dubbo.remoting.Client;
import com.study.dubbo.remoting.Transporter;
import com.study.dubbo.rpc.Invoker;
import com.study.dubbo.rpc.Response;
import com.study.dubbo.rpc.RpcInvocation;
import com.study.dubbo.rpc.protocol.Protocol;
import com.study.dubbo.rpc.protocol.wrpc.codec.WrpcCodec;
import com.study.dubbo.rpc.protocol.wrpc.handler.WrpcClientHandler;
import com.study.dubbo.rpc.protocol.wrpc.handler.WrpcServerHandler;

import java.net.URI;

/**
 * 协议的具体实现，通过该类启动Transporter网络框架对外提供网络服务
 */
public class WrpcProtocol implements Protocol {
    @Override
    public void export(URI exportUri, Invoker invoker) {
        //1.编解码器
        WrpcCodec wrpcCodec = new WrpcCodec();
        wrpcCodec.setDecodeType(RpcInvocation.class);
        String serializationName = URIUtils.getParam(exportUri, "serialization");
        Serialization serialization = SpiUtils.getServiceImpl(serializationName, Serialization.class);
        wrpcCodec.setSerialization(serialization);
        //2.处理器
        WrpcServerHandler wrpcServerHandler = new WrpcServerHandler(invoker,serialization);
        //3.通过配置文件指定的网络框架启动服务
        String transporterName = URIUtils.getParam(exportUri, "transporter");
        Transporter transporter = SpiUtils.getServiceImpl(transporterName, Transporter.class);
        transporter.start(exportUri,wrpcCodec,wrpcServerHandler);
    }

    @Override
    public Invoker refer(URI consumerUri) {
        //1.编解码器
        WrpcCodec wrpcCodec = new WrpcCodec();
        wrpcCodec.setDecodeType(Response.class);
        String serializationName = URIUtils.getParam(consumerUri, "serialization");
        Serialization serialization = SpiUtils.getServiceImpl(serializationName, Serialization.class);
        wrpcCodec.setSerialization(serialization);
        //2.处理器
        WrpcClientHandler wrpcClientHandler=new WrpcClientHandler();
        //3.通过配置文件指定的网络框架启动服务
        String transporterName = URIUtils.getParam(consumerUri, "transporter");
        Transporter transporter = SpiUtils.getServiceImpl(transporterName, Transporter.class);
        Client client = transporter.connect(consumerUri, wrpcCodec, wrpcClientHandler);

        //4.创建Invoker实现类，也就是被代理类
        WrpcClientInvoker wrpcClientInvoker=new WrpcClientInvoker(client,serialization);

        return wrpcClientInvoker;
    }


}
