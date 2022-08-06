package com.linxb.controller;

import com.linxb.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AlphaController {
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name,String age){
        System.out.println(name);
        System.out.println(age);
        System.out.println(CommunityUtil.getJsonString(0,"操作成功"));
        return CommunityUtil.getJsonString(0,"操作成功");
    }

    @RequestMapping(path = "/test2",method = RequestMethod.GET)
    public String test1(){
        return "/test";
    }
}
