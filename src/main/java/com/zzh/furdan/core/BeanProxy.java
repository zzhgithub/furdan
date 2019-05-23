package com.zzh.furdan.core;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;

public class BeanProxy implements MethodInterceptor {

    // FIXME :设置协议可以选择

    private Class<?> clazz;
    private RestTemplate restTemplate;
    private String appName;

    public Object createProxy(Class clazz, RestTemplate restTemplate, String appName) {
        this.clazz = clazz;
        this.restTemplate = restTemplate;
        this.appName = appName;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        Object proxyObj = enhancer.create();
        return proxyObj;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return restTemplate.postForObject("http://" + appName + "/" + clazz.getName() + "/" + method.getName(), objects, method.getReturnType());
    }
}
