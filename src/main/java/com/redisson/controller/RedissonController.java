package com.redisson.controller;

import com.redisson.compoent.lock.RedisLock;
import com.redisson.compoent.rateLimit.RedisRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedissonController {
    @Autowired
    private RedisLock redisLock;
    @Autowired
    private RedisRateLimiter redisRateLimiter;

    @RequestMapping("testLock")
    public String testLock(){
        redisLock.doSomethingWithLock();
        return "success";
    }

    @RequestMapping("testLimit")
    public String testLimit(){
        for (int i = 0; i < 100; i++) {
            redisRateLimiter.limitRate("testLimit");
        }
        return "success";
    }
}
