package com.study.dubbo.rpc.remoting;

import com.study.dubbo.remoting.Codec;
import com.study.dubbo.remoting.Handler;
import com.study.dubbo.remoting.WrpcChannel;
import com.study.dubbo.remoting.netty.Netty4Transporter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * 模拟基于Netty网络框架下使用自定义协议接收信息
 */
public class Netty4TransporterTest {
    public static void main(String[] args) throws URISyntaxException {
        new Netty4Transporter().start(new URI("WRPC://127.0.0.1:8080/"), new Codec() {
            @Override
            public byte[] encode(Object msg) throws Exception {
                return new byte[0];
            }

            @Override
            public List<Object> decode(byte[] data) throws Exception {
                System.out.println("解码器接收到的原始信息是："+new String(data));
                List<Object> list=new ArrayList<>();
                list.add("1");
                list.add("2");
                list.add("3");
                return list;
            }

            @Override
            public Codec createInstance() {
                return null;
            }
        }, new Handler() {
            @Override
            public void onReceive(WrpcChannel wrpcChannel, Object message) throws Exception {
                System.out.println("处理器接收到解码后的数据："+message);
            }

            @Override
            public void onWrite(WrpcChannel wrpcChannel, Object message) throws Exception {

            }
        });
    }
}
