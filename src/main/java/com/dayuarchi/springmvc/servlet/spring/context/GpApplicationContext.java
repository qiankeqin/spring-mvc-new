package com.dayuarchi.springmvc.servlet.spring.context;

import com.dayuarchi.springmvc.servlet.spring.bean.BeanDefinition;
import com.dayuarchi.springmvc.servlet.spring.core.BeanFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qiankeqin
 * @Description: 等同于Spring中的applicationContext
 * @date 2019-09-02 14:04
 */
public class GpApplicationContext implements BeanFactory {

    private BeanDefinitionReader reader;

    private String[] configLocations;

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public GpApplicationContext(String ...configLocations){
        this.configLocations = configLocations;
        this.refresh();
    }

    public void refresh(){
        //1.定位
        this.reader = new BeanDefinitionReader(configLocations);

        //2.加载
        List<String> beanNames = reader.loadBeanDefinitions();

        //3.注册
        // 将beanDefinition注入到beanDefinitionMap中
        doRegister(beanNames);

        //4.依赖注入
        //lazy-init = false，需要自动调用getBean进行依赖注入



    }

    /**
     * 注册过程
     * @param beanNames
     */
    private void doRegister(List<String> beanNames) {
        try{

            for (String classBean : beanNames) {
                Class<?> beanClass = Class.forName(classBean);

                //如果是一个接口，是不能实例化到
                if(beanClass.isInterface()){continue;}

                BeanDefinition beanDefinition = reader.registerBean(classBean);

                //1。默认是类名首字母小写
                if(null!=beanDefinition){
                    this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
                }

                //3。接口注入
                //如果有多个实现类，只能覆盖
                //为什么？Spring没有那么只能！
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> dependenceInterface : interfaces) {
                    this.beanDefinitionMap.put(dependenceInterface.getName(),beanDefinition);
                }

                //2。自定义名字【暂时不考虑】

                //初始化完成
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 依赖注入：从这里开始
     * 通过读取BeanDefinition中的信息
     * 然后，通过反射机制创建一个实例并返回
     * Spring的做法是，不会把最原始的对象放出去，这里会使用一个BeanWrapper来进行一次包装。
     * 包装器模式：
     * 1。保留原来的OOP关系
     * 2。需要对它进行扩展和增强，为以后的AOP打基础
     * @param name
     * @return
     */
    @Override
    public Object getBean(String name) {
        return null;
    }
}