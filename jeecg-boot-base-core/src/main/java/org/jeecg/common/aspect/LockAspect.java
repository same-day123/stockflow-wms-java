package org.jeecg.common.aspect;

import io.undertow.util.BadRequestException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jeecg.common.aspect.annotation.Lock;
import org.jeecg.common.util.AspectUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.lang.reflect.Method;

/**
 * @author Mr.M
 * @version 1.0
 * @description 分布式锁工具类
 * @date 2023/7/23 22:56
 */
@Aspect
public class LockAspect {

    private final RedissonClient redissonClient;

    public LockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(lock)")
    public Object handleLock(ProceedingJoinPoint pjp, Lock lock) throws Throwable {
        //锁key的格式化字符，此字符串中有spEL表达式
        String formatter = lock.formatter();
        Method method = AspectUtils.getMethod(pjp);
        Object[] args = pjp.getArgs();
        //得到锁的key
        String redisKey = AspectUtils.parse(formatter, method, args);
        //获取锁阻塞等待的时间,如果是0表示去尝试获取锁，如果获取不到则结束
        long waitTime = 0;
        //阻塞等待获取锁
        if (lock.block()) {
            //根据时间单位转换成ms
            waitTime = lock.waitTime();
        }
        //加锁时长
        long time = lock.time();
        //启动看门狗自动续期
        if(lock.startDog()){
            time = -1;
            //如果设置自动续期必须在方法执行后释放锁
            if(!lock.unlock()){
                throw new BadRequestException("请求参数不合法");
            }
        }
        //得到锁对象
        RLock rLock = redissonClient.getLock(redisKey);
        //尝试加锁
        boolean success = rLock.tryLock(waitTime,time, lock.unit());
        if (!success && !lock.block()) {
            //未阻塞要求的情况下未得到锁
            throw new BadRequestException("操作频繁,请稍后重试");
        }
        if (!success) {
            //阻塞情况下未得到锁，请求超时
            throw new RuntimeException("请求超时");
        }

        try {
            return pjp.proceed();
        } finally {
            if (lock.unlock()) {
                rLock.unlock();
            }
        }

    }

}