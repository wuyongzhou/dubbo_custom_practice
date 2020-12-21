package com.study.dubbo.config.spring;

import com.study.dubbo.config.ProtocolConfig;
import com.study.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Field;

/**
 * 把自己创建的对象放入Spring容器中作为一个SpringBean
 */
public class WRPCConfiguration implements ImportBeanDefinitionRegistrar {
    private StandardEnvironment environment;

    //对于ImportBeanDefinitionRegistrar的实现类可以通过构造函数获取Environment对象，获取配置的信息
    public WRPCConfiguration(Environment environment) {
        this.environment= (StandardEnvironment) environment;
    }

    /**
     * 让spring启动的时候装配，没有使用 注解/xml配置的类
     * 用代码的方式声明SpringBean
     * @param importingClassMetadata
     * @param registry
     * @param importBeanNameGenerator
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        BeanDefinitionBuilder beanDefinitionBuilder=null;

        //1. ProtocolConfig
        beanDefinitionBuilder=BeanDefinitionBuilder.genericBeanDefinition(ProtocolConfig.class);
        //trpc.protocol.xxx 属性注入，利用命名的规范性+反射获取动态的属性名
        for (Field field:ProtocolConfig.class.getDeclaredFields()){
            beanDefinitionBuilder.addPropertyValue(field.getName(),environment.getProperty("trpc.protocol."+field.getName()));
        }
        registry.registerBeanDefinition("protocolConfig",beanDefinitionBuilder.getBeanDefinition());


        //2. RegistryConfig
        beanDefinitionBuilder=BeanDefinitionBuilder.genericBeanDefinition(RegistryConfig.class);
        //trpc.registry.xxx 属性注入，利用命名的规范性+反射获取动态的属性名
        for (Field field:RegistryConfig.class.getDeclaredFields()){
            beanDefinitionBuilder.addPropertyValue(field.getName(),environment.getProperty("trpc.registry."+field.getName()));
        }
        registry.registerBeanDefinition("registryConfig",beanDefinitionBuilder.getBeanDefinition());

    }
}
