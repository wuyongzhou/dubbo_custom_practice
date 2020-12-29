package com.study.dubbo.order;

import com.study.dubbo.config.annotation.WRpcReference;
import com.study.dubbo.order.api.OrderService;
import com.study.dubbo.sms.api.SmsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    //@DubboReference
    @WRpcReference
    private SmsService smsService;

    public void create(String orderContent) {
        System.out.println("来了一个订单......"+orderContent);
        Object result = smsService.send("13902200634", orderContent);
        System.out.println("消费者收到的结果："+result.toString());
    }
}
