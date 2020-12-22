package com.study.dubbo.config.spring;

import com.study.dubbo.common.tools.SpiUtils;
import com.study.dubbo.config.ProtocolConfig;
import com.study.dubbo.config.RegistryConfig;
import com.study.dubbo.config.annotation.WRpcService;
import com.study.dubbo.remoting.Server;
import com.study.dubbo.remoting.Transporter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.URI;
import java.net.URISyntaxException;

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

            //如何知道当前配置文件指定使用哪种网络框架、哪种网络协议
            ProtocolConfig protocolConfig = applicationContext.getBean(ProtocolConfig.class);
            String transporterName = protocolConfig.getTransporter();
            Transporter transporter = SpiUtils.getServiceImpl(transporterName, Transporter.class);
            try {
                Server server = transporter.start(new URI("xxx://127.0.0.1:8080/"),null,null);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }

        /*if(bean.getClass().equals(RegistryConfig.class)){
            System.out.println("证明通过代码的方式可以成功将对象放入Spring容器中管理");
            System.out.println("当前配置的注册中心地址为："+((RegistryConfig)bean).getAddress());
        }*/
        return null;
    }
}
