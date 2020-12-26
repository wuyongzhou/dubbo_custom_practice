package com.study.dubbo.rpc.protocol;

import com.study.dubbo.common.serialize.json.JsonSerialization;
import com.study.dubbo.remoting.netty.Netty4Transporter;
import com.study.dubbo.rpc.RpcInvocation;
import com.study.dubbo.rpc.protocol.wrpc.codec.WrpcCodec;
import com.study.dubbo.rpc.protocol.wrpc.handler.WrpcServerHandler;

import java.net.URI;
import java.net.URISyntaxException;

public class WrpcProtocolTransporterTest {
    public static void main(String[] args) throws URISyntaxException {
        WrpcCodec wrpcCodec = new WrpcCodec();
        wrpcCodec.setDecodeType(RpcInvocation.class);
        wrpcCodec.setSerialization(new JsonSerialization());
        WrpcServerHandler wrpcServerHandler = new WrpcServerHandler(null,null);
        new Netty4Transporter().start(new URI("WRPC://127.0.0.1:8080/"),wrpcCodec,wrpcServerHandler);
    }
}
