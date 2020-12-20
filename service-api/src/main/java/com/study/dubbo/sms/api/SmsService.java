package com.study.dubbo.sms.api;

public interface SmsService {

    /**
     * 发送短信方法
     * @param phone 号码
     * @param content 内容
     * @return 发送结果
     */
    Object send(String phone, String content);
}
