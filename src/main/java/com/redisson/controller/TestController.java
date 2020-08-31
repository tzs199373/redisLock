package com.redisson.controller;

import com.redisson.compoent.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {
    @Autowired
    private RedisLock redisLock;

    @RequestMapping("test")
    public String test(){
        redisLock.doSomethingWithLock();
        return "success";
    }
}
