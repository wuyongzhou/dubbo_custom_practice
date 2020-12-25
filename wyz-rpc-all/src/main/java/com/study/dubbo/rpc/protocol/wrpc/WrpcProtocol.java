package com.study.dubbo.rpc.protocol.wrpc;

import com.study.dubbo.common.serialize.Serialization;
import com.study.dubbo.common.serialize.json.JsonSerialization;
import com.study.dubbo.common.tools.SpiUtils;
import com.study.dubbo.common.tools.URIUtils;
import com.study.dubbo.remoting.Transporter;
import com.study.dubbo.rpc.Invoker;
import com.study.dubbo.rpc.RpcInvocation;
import com.study.dubbo.rpc.protocol.Protocol;
import com.study.dubbo.rpc.protocol.wrpc.codec.WrpcCodec;
import com.study.dubbo.rpc.protocol.wrpc.handler.WrpcServerHandler;

import java.net.URI;

/**
 * 协议的具体实现，通过该类启动Transporter网络框架对外提供网络服务
 */
public class WrpcProtocol implements Protocol {
    @Override
    public void export(URI exportUri, Invoker invoker) {
        WrpcCodec wrpcCodec = new WrpcCodec();
        wrpcCodec.setDecodeType(RpcInvocation.class);
        String serializationName = URIUtils.getParam(exportUri, "serialization");
        Serialization serialization = SpiUtils.getServiceImpl(serializationName, Serialization.class);
        wrpcCodec.setSerialization(serialization);
        WrpcServerHandler wrpcServerHandler = new WrpcServerHandler(invoker);
        String transporterName = URIUtils.getParam(exportUri, "transporter");
        Transporter transporter = SpiUtils.getServiceImpl(transporterName, Transporter.class);
        transporter.start(exportUri,wrpcCodec,wrpcServerHandler);
    }
}
