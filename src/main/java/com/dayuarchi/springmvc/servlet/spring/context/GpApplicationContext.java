package com.dayuarchi.springmvc.servlet.spring.context;

import com.dayuarchi.springmvc.servlet.spring.annotation.GpAutowired;
import com.dayuarchi.springmvc.servlet.spring.annotation.GpController;
import com.dayuarchi.springmvc.servlet.spring.annotation.GpService;
import com.dayuarchi.springmvc.servlet.spring.bean.BeanDefinition;
import com.dayuarchi.springmvc.servlet.spring.bean.BeanPostProcessor;
import com.dayuarchi.springmvc.servlet.spring.bean.BeanWrapper;
import com.dayuarchi.springmvc.servlet.spring.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
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

    /**
     * 用于保存Bean的配置信息
     */
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    /**
     * 用来保证注册单例的容器
     */
    private Map<String,Object> beanCacheMap = new HashMap<>();

    /**
     * 存储所有的被代理过的对象
     */
    private Map<String,BeanWrapper> beanWrapperMap = new ConcurrentHashMap<>();

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
        doAutowired();


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
     * 开始执行自动化的依赖注入
     */
    private void doAutowired() {
        for(Map.Entry<String,BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                getBean(beanName);
            }
        }
    }


    public void polulateBean(String beanName ,Object instance){
        Class<?> clazz = instance.getClass();
        //不是所有牛奶都叫特仑苏
        //不是所有的类都要进行注入
        if(!(clazz.isAnnotationPresent(GpController.class) || clazz.isAnnotationPresent(GpService.class))){
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(GpAutowired.class)){
                continue;
            }

            GpAutowired autowired = field.getAnnotation(GpAutowired.class);
            String autowiredBeanName = autowired.value().trim();

            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }

            field.setAccessible(true);

            try {
                field.set(instance,beanWrapperMap.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
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
     * @param beanName
     * @return
     */
    @Override
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        String className = beanDefinition.getBeanClassName();
        try{
            //生成通知事件
            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();

            Object instance = instantionBean(beanDefinition);
            if(null == instance){
                return null;
            }
            //两次事件通知是固定的，不管你是否接受事件通知，都会去提醒。
            //就像点击鼠标，都会广播一边，不管你是否接收这个鼠标点击事件
            //在实例【初始化】以前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance,beanName);

            BeanWrapper beanWrapper = new BeanWrapper(instance);
            beanWrapper.setBeanPostProcessor(beanPostProcessor);
            //生成Bean的代理，并存放到beanWrapper中
            this.beanWrapperMap.put(className,beanWrapper);

            //在实例【初始化】以后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance,beanName);

            polulateBean(beanName,instance);

            //通过这里的调用，相当于给我们自己留有了可操作的空间
            return this.beanWrapperMap.get(className).getWrappedInstance();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 传入一个beanDefinition，就返回一个实例
     * @param beanDefinition bean定义
     * @return
     */
    private synchronized Object instantionBean(BeanDefinition beanDefinition){
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try{
            //因为根据class才能确定一个类是否有实例
            if(this.beanCacheMap.containsKey(className)){
                instance = beanCacheMap.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.beanCacheMap.put(className,instance);
            }
            return instance;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}