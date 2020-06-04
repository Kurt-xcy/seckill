package com.xcy.seckill.controller;

import com.xcy.seckill.pojo.User;
import com.xcy.seckill.redis.RedisService;
import com.xcy.seckill.result.Result;
import com.xcy.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/demo")
public class SampleController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;


    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","xcy");
        return "hello";
    }
    @RequestMapping("/select")
    public String select(Model model){
        User user = userService.getUser(1);
        model.addAttribute(user);
        return "select";
    }



}
