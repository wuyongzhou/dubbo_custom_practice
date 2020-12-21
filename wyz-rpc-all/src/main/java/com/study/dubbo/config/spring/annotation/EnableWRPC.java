package com.study.dubbo.config.spring.annotation;

import com.study.dubbo.config.spring.WRPCConfiguration;
import com.study.dubbo.config.spring.WRPCPostProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({WRPCPostProcessor.class, WRPCConfiguration.class})
public @interface EnableWRPC {

}
