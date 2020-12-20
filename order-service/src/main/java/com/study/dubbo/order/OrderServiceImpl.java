package com.study.dubbo.order;

import com.study.dubbo.order.api.OrderService;
import com.study.dubbo.sms.api.SmsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @DubboReference
    private SmsService smsService;

    public void create(String orderContent) {
        System.out.println("来了一个订单......"+orderContent);
        smsService.send("13902200634","我要淦亮亮");
    }
}
