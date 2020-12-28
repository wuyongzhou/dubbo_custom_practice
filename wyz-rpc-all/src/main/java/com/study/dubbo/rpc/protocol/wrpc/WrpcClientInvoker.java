package com.study.dubbo.rpc.protocol.wrpc;

import com.study.dubbo.common.serialize.Serialization;
import com.study.dubbo.common.tools.ByteUtil;
import com.study.dubbo.common.tools.SpiUtils;
import com.study.dubbo.rpc.Invoker;
import com.study.dubbo.rpc.RpcInvocation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class WrpcClientInvoker implements Invoker {
    @Override
    public Object getInterface() {
        return null;
    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) throws Exception {
        Serialization serialization = SpiUtils.getServiceImpl("JsonSerialization", Serialization.class);
        //得到body序列化后的字节数组
        byte[] bytes = serialization.serialize(rpcInvocation);

        //2. header 信息加上body数据
        ByteBuf byteBuf= Unpooled.buffer();
        byteBuf.writeByte(0xda);
        byteBuf.writeByte(0xbb);
        byteBuf.writeBytes(ByteUtil.int2bytes(bytes.length));
        byteBuf.writeBytes(bytes);

        //3. 模拟请求发送
        SocketChannel socketChannel=SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1",10088));
        //写出
        socketChannel.write(ByteBuffer.wrap(byteBuf.array()));
        //读取
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        socketChannel.read(byteBuffer);
        //读取完后，ByteBuffer处于写模式，需要翻转为读取模式，获取真实返回的消息数据长度
        byteBuffer.flip();
        byte[] content = new byte[byteBuffer.limit()];
        byteBuffer.get(content);
        String result=new String(content);
        System.out.println("调用返回信息："+result);
        return result;
    }
}
