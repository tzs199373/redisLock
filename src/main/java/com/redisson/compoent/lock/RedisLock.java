package com.redisson.compoent.lock;

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
            //���Լ��������ȴ�2�룬�����Ժ�100���Զ�����,����ѽ���ʱ�����ó�
            boolean res = lock.tryLock(2, 100, TimeUnit.SECONDS);
            if(res){ //�ɹ�
                //����ҵ��
                System.out.println("lock success");
                System.out.println("do something");
                Thread.sleep(1000*100);//ģ��ҵ����100s
            }else{
                System.out.println("not allow to do something");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(lock.isLocked()){ // �Ƿ�������״̬
                System.out.println("prepare to unlock");
                if(lock.isHeldByCurrentThread()){ // �Ƿ��ǵ�ǰִ���̵߳���
                    lock.unlock(); // �ͷ���
                    System.out.println("unlock success");
                }else{
                    System.out.println("not locked by current thread");//��Ӧ�������̲߳��ܽ���
                }
            }
        }
    }

}
