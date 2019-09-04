package com.dayuarchi.springmvc.servlet.spring.bean;

/**
 * @author qiankeqin
 * @Description: 用来存储配置文件中的信息
 * 详单于保存内存中的配置
 * @date 2019-09-03 09:06
 */
public class BeanDefinition {

    private String beanClassName;
    private String factoryBeanName;
    private boolean lazyInit = false;

    public void setBeanClassName(String beanClassName){
        this.beanClassName = beanClassName;
    }

    public String getBeanClassName(){

        return beanClassName;
    }


    public void setFactoryBeanName(String factoryBeanName){
        this.factoryBeanName = factoryBeanName;
    }

    public String getFactoryBeanName(){

        return factoryBeanName;
    }


    public void setLazyInit(boolean lazyInit){
        this.lazyInit = lazyInit;
    }

    public boolean isLazyInit(){
        return lazyInit;
    }


}