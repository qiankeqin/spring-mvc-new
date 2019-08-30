package com.dayuarchi.springmvc.servlet.spring.annotation;

import java.lang.annotation.*;

/**
 * @author qiankeqin
 * @Description: requestMapping注解
 * @date 2019-08-29 15:14
 */
@Target({ElementType.TYPE,ElementType.METHOD})//作用域类/方法
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GpRequestMapping {

    String value() default "";

}
