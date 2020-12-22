package com.study.dubbo.rpc.protocol.wrpc;

import com.study.dubbo.common.tools.SpiUtils;
import com.study.dubbo.common.tools.URIUtils;
import com.study.dubbo.remoting.Transporter;
import com.study.dubbo.rpc.protocol.Protocol;

import java.net.URI;

public class WrpcProtocol implements Protocol {
    @Override
    public void export(URI exportUri) {
        String transporterName = URIUtils.getParam(exportUri, "transporter");
        Transporter transporter = SpiUtils.getServiceImpl(transporterName, Transporter.class);
        transporter.start(exportUri,null,null);
    }
}
