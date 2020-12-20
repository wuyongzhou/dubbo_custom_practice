package com.study.dubbo;

import com.study.dubbo.config.spring.annotation.EnableWRPC;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

@Configuration
@ComponentScan("com.study.dubbo")
//@EnableDubbo(scanBasePackages = "com.study.dubbo")
//@PropertySource("classpath:/dubbo.properties")
@PropertySource("classpath:/trpc.properties")
@EnableWRPC
public class SmsApplication {

    public static void main(String[] args) throws IOException {
        final AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(SmsApplication.class);
        context.start();


        // 阻塞不退出
        System.in.read();
        context.close();
    }
}
