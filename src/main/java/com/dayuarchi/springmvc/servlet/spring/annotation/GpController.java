package com.dayuarchi.springmvc.servlet.spring.annotation;


import java.lang.annotation.*;

/**
 * @author qiankeqin
 * @Description: controller注解
 * @date 2019-08-29 15:09
 */
@Target({ElementType.TYPE})//在类上使用
@Retention(RetentionPolicy.RUNTIME)//运行时生效
@Documented
public @interface GpController {

    String value() default "";

}
