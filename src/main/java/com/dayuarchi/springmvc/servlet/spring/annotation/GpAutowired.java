package com.dayuarchi.springmvc.servlet.spring.annotation;

import java.lang.annotation.*;

/**
 * @author qiankeqin
 * @Description: DESCRIPTION
 * @date 2019-08-29 15:29
 */
@Target({ElementType.FIELD})//在类上使用
@Retention(RetentionPolicy.RUNTIME)//运行时生效
@Documented
public @interface GpAutowired {

    String value() default "";
}
