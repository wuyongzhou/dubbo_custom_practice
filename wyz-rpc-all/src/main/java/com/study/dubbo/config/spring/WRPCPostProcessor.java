package com.study.dubbo.config.spring;

import com.study.dubbo.config.ProtocolConfig;
import com.study.dubbo.config.ReferenceConfig;
import com.study.dubbo.config.RegistryConfig;
import com.study.dubbo.config.ServiceConfig;
import com.study.dubbo.config.annotation.WRpcReference;
import com.study.dubbo.config.annotation.WRpcService;
import com.study.dubbo.config.util.WprcBootstrap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;

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
            System.out.println("发现服务提供者，开始启动网络服务，接受请求");
            System.out.println("开始构建服务配置对象....");
            ServiceConfig serviceConfig=new ServiceConfig();
            serviceConfig.addProtocolConfig(applicationContext.getBean(ProtocolConfig.class));
            serviceConfig.addRegistryConfig(applicationContext.getBean(RegistryConfig.class));
            serviceConfig.setReference(bean);
            WRpcService annotation = bean.getClass().getAnnotation(WRpcService.class);
            if(void.class==annotation.interfaceClass()){
                serviceConfig.setService(bean.getClass().getInterfaces()[0]);
            }else{
                serviceConfig.setService(annotation.interfaceClass());
            }
            //借助Bootstrap工具类暴露发布服务
            WprcBootstrap.export(serviceConfig);

        }

        //2.服务消费者，需要遍历
        for (Field field : bean.getClass().getDeclaredFields()) {
            try {
                //只对修饰了消费者注解的属性操作
                if(!field.isAnnotationPresent(WRpcReference.class)){
                    continue;
                }
                ReferenceConfig referenceConfig=new ReferenceConfig();
                referenceConfig.addProtocolConfig(applicationContext.getBean(ProtocolConfig.class));
                referenceConfig.addRegistryConfig(applicationContext.getBean(RegistryConfig.class));
                referenceConfig.setService(field.getType());

                Object referenceBean = WprcBootstrap.getReferenceBean(referenceConfig);
                //私有属性
                if(!field.isAccessible()){
                    field.setAccessible(true);
                }
                field.set(bean,referenceBean);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return bean;
    }
}
