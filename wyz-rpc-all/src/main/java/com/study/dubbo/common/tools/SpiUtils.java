package com.study.dubbo.common.tools;

import java.util.ServiceLoader;

public class SpiUtils {
    public static <T> T getServiceImpl(String serviceName, Class<T> classType) {
        ServiceLoader<T> services = ServiceLoader.load(classType, Thread.currentThread().getContextClassLoader());
        // 根据服务定义的协议，依次暴露。 如果有多个协议那就暴露多次
        for (T s : services) {
            if (s.getClass().getSimpleName().equals(serviceName)) {
                return s;
            }
        }
        return null;
    }
}
