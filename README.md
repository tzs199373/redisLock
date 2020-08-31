# 使用redisson实现分布式锁

准备

本地安装redis

依赖

```xml
<dependency>
			<groupId>org.redisson</groupId>
			<artifactId>redisson</artifactId>
			<version>3.8.2</version>
		</dependency>
```

配置redis连接

```java
package com.redisson.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class RedissonConfig {
    @Bean
    public RedissonClient getClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
```

加锁实现

```java
package com.redisson.compoent;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author tzs
 * @version 1.0
 * @Description
 * @since 2020/8/31 10:46
 */
@Component
public class RedisLock {
    @Autowired
    private RedissonClient redisson;

    public void doSomethingWithLock() {
        RLock lock = redisson.getLock("lockName");
        try{
            // 1. 最常见的使用方法
            //lock.lock();
            // 2. 支持过期解锁功能,10秒钟以后自动解锁, 无需调用unlock方法手动解锁
            //lock.lock(10, TimeUnit.SECONDS);


            // 3. 尝试加锁，最多等待2秒，上锁以后100秒自动解锁,故意把解锁时间设置长
            boolean res = lock.tryLock(2, 100, TimeUnit.SECONDS);
            if(res){ //成功
                //处理业务
                System.out.println("lock success");
                System.out.println("do something");
                Thread.sleep(1000*100);//模拟业务处理100s
            }else{
                System.out.println("not allow to do something");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(lock.isLocked()){ // 是否还是锁定状态
                System.out.println("prepare to unlock");
                if(lock.isHeldByCurrentThread()){ // 是否是当前执行线程的锁
                    lock.unlock(); // 释放锁
                    System.out.println("unlock success");
                }else{
                    System.out.println("not locked by current thread");//上锁的线程才能解锁
                }
            }
        }
    }

}

```

​	在多个进程中测试doSomethingWithLock方法，比如用IDEA，更改springboot配置的端口号，同一个工程启动两个实例，这里笔者故意设置100s的业务处理，请求一个进程比如8080，加锁，再请求另一个进程，比如8081，观察输出。

​	随便写个请求接收方法

```java
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

```

