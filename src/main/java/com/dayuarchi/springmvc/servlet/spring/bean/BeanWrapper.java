package com.dayuarchi.springmvc.servlet.spring.bean;

import com.dayuarchi.springmvc.servlet.spring.core.FactoryBean;

/**
 * @author qiankeqin
 * @Description: bean的包装器类
 * 所有的类都是FactoryBean
 * @date 2019-09-03 09:06
 */
public class BeanWrapper extends FactoryBean {

    /**
     *还会用到  观察者 模式
     *支持时间响应，这里会用到监听
     */
    private BeanPostProcessor beanPostProcessor;

    private Object wrapperInstance;
    //
    private Object originnalInstance;

    public BeanWrapper(Object instance) {
        this.wrapperInstance = instance;
        this.originnalInstance = instance;
    }

    /**
     * 返回包装后的实例
     * @return
     */
    public  Object getWrappedInstance(){
        return wrapperInstance;
    }

    /**
     * 返回未包装的实例
     * @return
     */
    public Object getOriginnalInstance(){
        return originnalInstance;
    }

    /**
     * 返回代理以后的className，通常代理后包含$
     * @return
     */
    public Class<?> getWrappedClass(){
        return wrapperInstance.getClass();
    }

    public BeanPostProcessor getBeanPostProcessor() {
        return beanPostProcessor;
    }

    public void setBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessor = beanPostProcessor;
    }
}