package com.redisson.compoent.rateLimit;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RedisRateLimiter {
    @Autowired
    private RedissonClient redisson;

    public void limitRate(String key) {
        if (!StringUtils.isEmpty(key)) {
            RRateLimiter rateLimiter = redisson.getRateLimiter(key);
            rateLimiter.trySetRate(RateType.OVERALL, 10, 1, RateIntervalUnit.SECONDS);//ÿ1�����10������
            if (rateLimiter.tryAcquire(1)) {
                System.out.println("ִ��һ��ҵ��");
            }
        }
    }
}
