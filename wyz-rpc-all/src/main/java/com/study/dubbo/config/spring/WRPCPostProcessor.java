package com.study.dubbo.config.spring;

import com.study.dubbo.config.annotation.WRpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 针对自定义注解的发现与解析
 */
public class WRPCPostProcessor implements ApplicationContextAware, InstantiationAwareBeanPostProcessor {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //1.服务提供者
        if(bean.getClass().isAnnotationPresent(WRpcService.class)){
            System.out.println("启动网络服务，接受请求");
        }
        return null;
    }
}
