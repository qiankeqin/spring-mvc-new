package com.dayuarchi.springmvc.servlet.demo.mvc.action;

import com.dayuarchi.springmvc.servlet.demo.service.IDemoService;
import com.dayuarchi.springmvc.servlet.spring.annotation.GpAutowired;
import com.dayuarchi.springmvc.servlet.spring.annotation.GpController;
import com.dayuarchi.springmvc.servlet.spring.annotation.GpRequestMapping;

/**
 * @author qiankeqin
 * @Description: DESCRIPTION
 * @date 2019-08-29 15:41
 */
@GpController
public class MyAction {
    @GpAutowired
    private IDemoService demoService;

    @GpRequestMapping("/index.html")
    public void query(){

    }
}