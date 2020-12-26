package com.study.dubbo.sms;

import com.study.dubbo.config.annotation.WRpcReference;
import com.study.dubbo.config.annotation.WRpcService;
import com.study.dubbo.sms.api.SmsService;
import org.apache.dubbo.config.annotation.DubboService;

//@DubboService

/**
 * 网络服务提供者具体的业务逻辑
 * 一个提供者会包含N个方法，这些方法通过反射机制调用，在手写RPC框架中可以获取到提供者的接口，以实现反射时的静态代理处理。
 */
@WRpcService
public class SmsServiceImpl implements SmsService {

    public Object send(String phone, String content) {
        System.out.println("发送短信，phone："+phone+"，content："+content);
        return "成功发送短信----";
    }
}
