package com.study.dubbo.config.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface WRpcService {

    /**
     * 如果有多个接口，需要自己指定一个
     */
    Class<?> interfaceClass() default void.class;
}
