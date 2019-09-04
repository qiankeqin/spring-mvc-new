package com.dayuarchi.springmvc.servlet.spring.core;

/**
 * @author qiankeqin
 * @Description: DESCRIPTION
 * @date 2019-09-02 14:12
 */
public interface BeanFactory {

    /**
     * 根据beanName从IOC容器之中获得一个实例Bean
     * @param name
     * @return
     */
    Object getBean(String name);
}