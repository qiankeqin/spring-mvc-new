package com.dayuarchi.springmvc.servlet.spring.context;

import com.dayuarchi.springmvc.servlet.spring.SpringUtils;
import com.dayuarchi.springmvc.servlet.spring.bean.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author qiankeqin
 * @Description: Reader的功能：对配置文件进行查找读取解析
 * @date 2019-09-03 09:04
 */
public class BeanDefinitionReader {

    private Properties config = new Properties();

    private List<String> registerBeanClasses = new ArrayList<>();

    private List<BeanDefinition> registerBeanDefinitions = new ArrayList<>();

    //从配置文件中获取扫描包对路径
    private final String SCAN_PACKAGE = "";

    public BeanDefinitionReader(String ...locations) {
        //定位资源，加载到InputStream中
        //1.在Spring中是通过Reader来查找和定位
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));

        try {
            //加载资源到config中
            config.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null!=in){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public BeanDefinition registerBean(String classBean){
        //每次加载完类，都将类包装成BeanDefinition
        if(this.registerBeanClasses.contains(classBean)){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(classBean);
            beanDefinition.setFactoryBeanName(SpringUtils.lowerFirstCase(classBean.substring(classBean.lastIndexOf(".")+1)));
            //这里默认是false，可以不设置
            beanDefinition.setLazyInit(false);
            return beanDefinition;
        }
        return null;
    }


    /**
     * 2）IOC的加载：加载资源文件
     * 递归加载bean
     * 这里主要是将所有的类加载进来，以备后面的使用
     */
    private void doLoadResource(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replace(".","/"));

        File classDir = new File(url.getFile());

        for (File file : classDir.listFiles()) {
            if(file.isDirectory()){
                doLoadResource(packageName + "." + file.getName());
            } else {
                registerBeanClasses.add(packageName + "." + file.getName().replace(".class",""));
            }
        }
    }

    /**
     * 加载所有的Bean的信息，并返回List
     * @return
     */
    public List<String> loadBeanDefinitions(){
        doLoadResource(config.getProperty("scanPackage"));
        return registerBeanClasses;
    }

    public List<String> getRegisterBeanClasses(){
        return null;
    }

    public Properties getConfig(){
        return config;
    }
}