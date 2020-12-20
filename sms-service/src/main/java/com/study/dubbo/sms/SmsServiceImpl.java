package com.study.dubbo.sms;

import com.study.dubbo.config.annotation.WRpcReference;
import com.study.dubbo.config.annotation.WRpcService;
import com.study.dubbo.sms.api.SmsService;
import org.apache.dubbo.config.annotation.DubboService;

//@DubboService
@WRpcService
public class SmsServiceImpl implements SmsService {

    public Object send(String phone, String content) {
        System.out.println("发送短信，phone："+phone+"，content："+content);
        return "成功发送短信----";
    }
}
