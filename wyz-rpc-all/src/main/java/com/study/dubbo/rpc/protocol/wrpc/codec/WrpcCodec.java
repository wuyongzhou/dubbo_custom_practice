package com.study.dubbo.rpc.protocol.wrpc.codec;

import com.study.dubbo.common.serialize.Serialization;
import com.study.dubbo.common.tools.ByteUtil;
import com.study.dubbo.remoting.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.List;

public class WrpcCodec implements Codec {
    //协议头部特殊标识符  0xdabb
    public final static byte[] MAGIC = new byte[]{(byte) 0xda, (byte) 0xbb};
    //协议头部长度
    public static final int HEADER_LEN = 6;

    private ByteBuf tempMsg= Unpooled.buffer();

    private Serialization serialization;
    private Class decodeType;

    public Serialization getSerialization() {
        return serialization;
    }

    public void setSerialization(Serialization serialization) {
        this.serialization = serialization;
    }

    public Class getDecodeType() {
        return decodeType;
    }

    public void setDecodeType(Class decodeType) {
        this.decodeType = decodeType;
    }

    @Override
    public byte[] encode(Object msg) throws Exception {
        return new byte[0];
    }

    @Override
    public List<Object> decode(byte[] data) throws Exception {
        List<Object> list=new ArrayList<>();
        ByteBuf message= Unpooled.buffer();
        //1.判断tempMsg中是否残留数据，若有则合并
        if(tempMsg.isReadable()){
            message.writeBytes(tempMsg);
            message.writeBytes(data);
            System.out.println("有残留数据------");
        }else{
            message.writeBytes(data);
        }

        //2.开始尝试解析出一个完整的invocation对象
        for (;;){

            //2.1 数据不够头部信息长度，放入缓存等待下次合并处理
            if(HEADER_LEN>=message.readableBytes()){
                tempMsg.clear();
                tempMsg.writeBytes(message);
                return list;
            }

            //2.2 校验头部信息的特殊字符
            byte[] magic=new byte[2];
            message.readBytes(magic);
            for (;;){
                //数据开头不是连续包含 例如：dabb
                if(magic[0]!=MAGIC[0]||magic[1]!=MAGIC[1]){
                    //表示读取到尾部了，保留最后一个字节到缓存中，等待下次操作
                    if(!message.isReadable()){
                        tempMsg.clear();
                        tempMsg.writeByte(magic[1]);
                        return list;
                    }
                    magic[0]=magic[1];
                    magic[1]=message.readByte();
                }else{
                    break;
                }
            }

            //2.3 获取Body中的长度，截取对应的Body信息
            byte[] lengthBytes = new byte[4];
            message.readBytes(lengthBytes);
            int bodyLength = ByteUtil.Bytes2Int_BE(lengthBytes);
            //当前可读取的内容不足信息头中指定的Body长度，认为数据不完整，放到缓存中，等待下次操作
            if(message.readableBytes()<bodyLength){
                tempMsg.clear();
                tempMsg.writeBytes(magic);
                tempMsg.writeBytes(lengthBytes);
                tempMsg.writeBytes(message);
                return list;
            }
            //至少满足一个完整的数据长度，有一个添加一个
            byte[] body = new byte[bodyLength];
            message.readBytes(body);
            Object o = getSerialization().deserialize(body, decodeType);
            list.add(o);
        }
    }

    @Override
    public Codec createInstance() {
        WrpcCodec codec=new WrpcCodec();
        codec.setDecodeType(this.decodeType);
        codec.setSerialization(this.serialization);
        return codec;
    }
}
