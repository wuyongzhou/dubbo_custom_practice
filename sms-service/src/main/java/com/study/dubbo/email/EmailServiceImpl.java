package com.study.dubbo.email;

import com.study.dubbo.config.annotation.WRpcService;
import com.study.dubbo.sms.api.SmsService;

/**
 * 目前手写RPC框架有缺陷，如果一个应用中存在多个服务提供者会报错，端口已存在冲突，因为一个服务占用一个网络端口
 */
//@DubboService
//@WRpcService
public class EmailServiceImpl implements SmsService {

    public Object send(String phone, String content) {
        System.out.println("发送邮件，phone："+phone+"，content："+content);
        return "成功发送邮件----";
    }
}
