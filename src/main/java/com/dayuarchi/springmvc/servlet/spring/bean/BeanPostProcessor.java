package com.dayuarchi.springmvc.servlet.spring.bean;

/**
 * @author qiankeqin
 * @Description: 用于做消费监听
 * @date 2019-09-04 09:47
 */
public class BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}