package com.dayuarchi.springmvc.servlet.demo.mvc.action;

import com.dayuarchi.springmvc.servlet.demo.service.IDemoService;
import com.dayuarchi.springmvc.servlet.spring.annotation.GpAutowired;
import com.dayuarchi.springmvc.servlet.spring.annotation.GpController;
import com.dayuarchi.springmvc.servlet.spring.annotation.GpRequestMapping;
import com.dayuarchi.springmvc.servlet.spring.annotation.GpRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author qiankeqin
 * @Description: DESCRIPTION
 * @date 2019-08-29 15:32
 */
@GpController
@GpRequestMapping("/demo")
public class DemoAction {

    //使用Autowired进行注入，
    @GpAutowired
    private IDemoService demoService;

    @GpRequestMapping("/query.json")
    public void query(HttpServletRequest request, HttpServletResponse resp, @GpRequestParam("name") String name){
        String result = demoService.get(name);
        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @GpRequestMapping("/edit.json")
    public void edit(HttpServletRequest request,HttpServletResponse resp,@GpRequestParam("id") Integer id){
        //浙江
    }

}