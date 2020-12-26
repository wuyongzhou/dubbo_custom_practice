package com.study.dubbo.rpc.protocol.wrpc.handler;

import com.study.dubbo.common.serialize.Serialization;
import com.study.dubbo.remoting.Handler;
import com.study.dubbo.remoting.WrpcChannel;
import com.study.dubbo.rpc.Invoker;
import com.study.dubbo.rpc.Response;
import com.study.dubbo.rpc.RpcInvocation;

import java.net.URI;

/**
 * 真正执行业务处理的类
 */
public class WrpcServerHandler implements Handler {

    private Invoker invoker;
    private Serialization serialization;

    public WrpcServerHandler(Invoker invoker,Serialization serialization) {
        this.invoker = invoker;
        this.serialization=serialization;
    }

    @Override
    public void onReceive(WrpcChannel wrpcChannel, Object message) throws Exception {
        Response response=new Response();
        RpcInvocation rpcInvocation= (RpcInvocation) message;
        System.out.println("收到rpcInvocation信息："+rpcInvocation);
        //执行客户端请求，得到结果后，对客户端请求做出响应
        response.setRequestId(rpcInvocation.getId());
        try {
            Object result = invoker.invoke(rpcInvocation);
            System.out.println("服务端执行结果："+result);
            response.setStatus(200);
            response.setContent(result);
        }catch (Throwable throwable){
            throwable.printStackTrace();
            response.setStatus(99);
            response.setContent(throwable.getMessage());
        }

        //课程视频代码
        /*//发送结果数据
        byte[] bodyBytes = serialization.serialize(response);
        wrpcChannel.send(bodyBytes);*/

        //虽然课程视频是在这里直接写回，但是这样就没有利用到onWrite这个方法了，下面是个人理解稍微改了一下
        //在onWrite方法里面再写回
        this.onWrite(wrpcChannel,response);
    }

    @Override
    public void onWrite(WrpcChannel wrpcChannel, Object message) throws Exception {
        /**
         *  【个人理解】
         *  handler就是处理接收/写出的逻辑，就好像onReceive方法的message参数，在方法中就可以确定是一个RpcInvocation对象了。
         *  这是因为在上一个Handler，也就是WrpcCodec对象中将数组数据转为了java对象，这是接收的逻辑。
         *  同理，写出也应该遵循一样的风格，在WrpcCodec对象中才要将java对象转为数组数据，这里由于自定义channel的send方法需要byte数组，先这样做，对于效果没有影响。
         */
        byte[] bodyBytes = serialization.serialize(message);
        wrpcChannel.send(bodyBytes);
    }
}
