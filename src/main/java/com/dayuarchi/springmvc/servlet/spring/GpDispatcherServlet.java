package com.dayuarchi.springmvc.servlet.spring;

import com.dayuarchi.springmvc.servlet.demo.mvc.action.DemoAction;
import com.dayuarchi.springmvc.servlet.spring.annotation.*;
import com.dayuarchi.springmvc.servlet.spring.context.GpApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qiankeqin
 * @Description: DESCRIPTION
 * @date 2019-08-28 08:56
 */
public class GpDispatcherServlet extends HttpServlet {

    //配置文件
    private Properties contextConfig = new Properties();

    //所有带注解（Spring中还有通过其他方式注入的，如xml）
    private Map<String,Object> beanMap = new ConcurrentHashMap<>();

    //扫描路径下面所有的类
    private List<String> classNames = new ArrayList<>();

    private final String LOCATION = "contextConfigLocation";


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("----------调用doPost------------");
        System.out.println("----------开始初始化------------");

    }

    /**
     * spring 2.0 init方法
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        GpApplicationContext context = new GpApplicationContext(config.getInitParameter(LOCATION));
    }


    //spring 1。0版本
//    @Override
//    public void init(ServletConfig config) throws ServletException {
//        //开始初始化的进程
//
//        //1）IOC 资源定位
//        //这里加载到我们的配置文件properties文件下的scanPackage，就是说拿到我们的location地址了。
//        //后面开始扫描
//        doLocateResource(config.getInitParameter("contextConfigLocation"));
//
//        //加载
//        doLoadResource(contextConfig.getProperty("scanPackage"));
//
//        //注册
//        doRegister();
//
//        //自动依赖注入，调用getBean出发依赖注入/或者lazy-init=false自动加载注入
//        doAutowired(beanMap);
//
//        //如果是SpringMVC，多设计一个HandlerMapping
//        //将@RequestMapping中配置的url和一个Method关联上
//        //initMappingHandler();
//
//        DemoAction demoAction = (DemoAction) beanMap.get("demoAction");
//        demoAction.query(null,null,"XXX");
//    }


    /**
     * 1）IOC的定位：定位bean配置文件
     * @param location
     */
    private void doLocateResource(String location) {
        //1.在Spring中是通过Reader来查找和定位
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:",""));

        try {
            contextConfig.load(in);
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


    /**
     * 2）IOC的加载：加载资源文件
     * 这里主要是将所有的类加载进来，以备后面的使用
     */
    private void doLoadResource(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replace(".","/"));

        File classDir = new File(url.getFile());

        for (File file : classDir.listFiles()) {
            if(file.isDirectory()){
                doLoadResource(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." + file.getName().replace(".class",""));
            }
        }
    }

    private void doRegister() {
        if(null==classNames || classNames.isEmpty()){
            return ;
        }
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                //判断annotation是否存在
                if(clazz.getAnnotations()!=null && clazz.getAnnotations().length>0){
                    if(clazz.isAnnotationPresent(GpController.class)){
                        System.out.println("加载Controller"+clazz.getName());
                        String beanName = lowerFirstCase(clazz.getSimpleName());
                        //在Spring中，这里是不会直接put instance，这里put的是BeanDefinition
                        beanMap.put(beanName,clazz.newInstance());
                    }
                    if(clazz.isAnnotationPresent(GpService.class)){
                        System.out.println("加载Service"+clazz.getName());
                        GpService serviceAnnotation = clazz.getAnnotation(GpService.class);
                        //默认用类名首字母注入
                        //如果自己定义了beanName，那么优先使用自己定义的beanName
                        //如果是一个接口，那么使用接口的类型去自动注入

                        //在Spring中，上面会使用不同的方法去注入 autowiredType,autowiredName
                        //所以这里要注册的时候，既要类型注入/也要名称注入
                        String beanName = serviceAnnotation.value();
                        if("".equals(beanName.trim())){
                            beanName = lowerFirstCase(clazz.getSimpleName());
                        }
                        Object instance = clazz.newInstance();
                        beanMap.put(beanName,instance);

                        Class<?>[] interfaces = clazz.getInterfaces();
                        for (Class<?> anInterface : interfaces) {
                            beanMap.put(anInterface.getSimpleName(),instance);
                        }
                    }
                    if(clazz.isAnnotationPresent(GpRequestMapping.class)){
                        System.out.println("加载RequestMapping"+clazz.getName());

                    }
                    if(clazz.isAnnotationPresent(GpRequestParam.class)){
                        System.out.println("加载RequestParam"+clazz.getName());

                    }
                    if(clazz.isAnnotationPresent(GpAutowired.class)){
                        System.out.println("加载Autowired"+clazz.getName());

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 依赖注入过程
     * @param beanMap
     */
    private void doAutowired(Map<String, Object> beanMap) {
        if(beanMap==null || beanMap.isEmpty()){
            return;
        }

        //获取到所有Spring中管理的bean，开始对Bean进行依赖注入
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if(!field.isAnnotationPresent(GpAutowired.class)){
                    continue;
                }
                GpAutowired autowired = field.getAnnotation(GpAutowired.class);
                String dependenceBeanName = autowired.value().trim();
                //如果名称为空，那么使用类型注入
                if("".equals(dependenceBeanName)){
                    dependenceBeanName = field.getType().getSimpleName();
                }

                field.setAccessible(true);
                //设置[Spring Bean]entry.getValue中的属性field的值
                try {
                    field.set(entry.getValue(),beanMap.get(dependenceBeanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private String lowerFirstCase(String simpleName) {
        if(null==simpleName || simpleName.length()==0){
            return "";
        }
        if(simpleName.length() == 1){
            return simpleName.toLowerCase();
        }
        return simpleName.substring(0,1).toLowerCase() + simpleName.substring(1);
    }

}