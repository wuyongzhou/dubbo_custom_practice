package com.study.dubbo.order.api;

public interface OrderService {

    /**
     * 创建订单
     *
     * @param orderContent 订单内容
     */
    void create(String orderContent);
}
