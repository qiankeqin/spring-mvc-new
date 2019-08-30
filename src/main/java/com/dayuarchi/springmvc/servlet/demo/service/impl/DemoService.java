package com.dayuarchi.springmvc.servlet.demo.service.impl;

import com.dayuarchi.springmvc.servlet.demo.service.IDemoService;
import com.dayuarchi.springmvc.servlet.spring.annotation.GpService;

/**
 * @author qiankeqin
 * @Description: DESCRIPTION
 * @date 2019-08-29 15:34
 */
@GpService
public class DemoService implements IDemoService {
    @Override
    public String get(String name) {
        return "my name is "+name;
    }
}