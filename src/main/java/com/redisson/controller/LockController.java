package com.redisson.controller;

import com.redisson.compoent.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LockController {
    @Autowired
    private RedisLock redisLock;

    @RequestMapping("testLock")
    public String testLock(){
        redisLock.doSomethingWithLock();
        return "success";
    }
}
