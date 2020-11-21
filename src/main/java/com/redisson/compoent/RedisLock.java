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
            //尝试加锁，最多等待2秒，上锁以后100秒自动解锁,故意把解锁时间设置长
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
                    System.out.println("not locked by current thread");//对应上锁的线程才能解锁
                }
            }
        }
    }

}
