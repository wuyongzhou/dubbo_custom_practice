package com.study.dubbo;

import com.study.dubbo.config.spring.annotation.EnableWRPC;
import com.study.dubbo.order.api.OrderService;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.study.dubbo")
@EnableDubbo(scanBasePackages = "com.study.dubbo")
//@PropertySource("classpath:/dubbo.properties")
@PropertySource("classpath:/trpc.properties")
@EnableWRPC
public class OrderApplication {
    public static void main(String[] args) throws Exception {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(OrderApplication.class);
        context.start();

        OrderService orderService = context.getBean(OrderService.class);
        orderService.create("测试一下啦---");

        /*final CyclicBarrier cyclicBarrier = new CyclicBarrier(1);
        for (int i = 0; i < 1; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        cyclicBarrier.await();
                        // 测试..模拟调用接口 -- 一定是远程，因为当前的系统没有具体实现类
                        OrderService orderService = context.getBean(OrderService.class);
                        orderService.create("买一瓶水");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }*/

        // 阻塞不退出
        System.in.read();
        context.close();
    }
}
