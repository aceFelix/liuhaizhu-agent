package com.itfelix.liuhaizhuaichat.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁工具类
 * 用于解决并发场景下的资源竞争问题
 * @author aceFelix
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLockUtil {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_WAIT_TIME = 0;
    private static final long DEFAULT_LEASE_TIME = 30;

    /**
     * 尝试获取分布式锁
     * @param lockKey 锁的key
     * @param leaseTime 锁的持有时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long leaseTime) {
        return tryLock(lockKey, DEFAULT_WAIT_TIME, leaseTime);
    }

    /**
     * 尝试获取分布式锁
     * @param lockKey 锁的key
     * @param waitTime 等待时间（秒）
     * @param leaseTime 锁的持有时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        String fullKey = LOCK_PREFIX + lockKey;
        String threadId = String.valueOf(Thread.currentThread().getId());
        
        long startTime = System.currentTimeMillis();
        long waitTimeMs = waitTime * 1000;
        
        while (true) {
            Boolean success = stringRedisTemplate.opsForValue()
                    .setIfAbsent(fullKey, threadId, leaseTime, TimeUnit.SECONDS);
            
            if (Boolean.TRUE.equals(success)) {
                log.debug("获取分布式锁成功: key={}, threadId={}", fullKey, threadId);
                return true;
            }
            
            if (System.currentTimeMillis() - startTime >= waitTimeMs) {
                log.warn("获取分布式锁超时: key={}, waitTime={}s", fullKey, waitTime);
                return false;
            }
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("获取分布式锁被中断: key={}", fullKey);
                return false;
            }
        }
    }

    /**
     * 释放分布式锁
     * @param lockKey 锁的key
     * @return 是否释放成功
     */
    public boolean unlock(String lockKey) {
        String fullKey = LOCK_PREFIX + lockKey;
        String threadId = String.valueOf(Thread.currentThread().getId());
        
        String currentValue = stringRedisTemplate.opsForValue().get(fullKey);
        if (threadId.equals(currentValue)) {
            Boolean deleted = stringRedisTemplate.delete(fullKey);
            if (Boolean.TRUE.equals(deleted)) {
                log.debug("释放分布式锁成功: key={}, threadId={}", fullKey, threadId);
                return true;
            }
        }
        
        log.warn("释放分布式锁失败: key={}, threadId={}, lockHolder={}", fullKey, threadId, currentValue);
        return false;
    }

    /**
     * 检查锁是否存在
     * @param lockKey 锁的key
     * @return 是否存在
     */
    public boolean isLocked(String lockKey) {
        String fullKey = LOCK_PREFIX + lockKey;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(fullKey));
    }

    /**
     * 使用分布式锁执行任务
     * @param lockKey 锁的key
     * @param leaseTime 锁的持有时间（秒）
     * @param task 要执行的任务
     * @return 任务是否执行成功
     */
    public boolean executeWithLock(String lockKey, long leaseTime, Runnable task) {
        if (!tryLock(lockKey, leaseTime)) {
            log.warn("获取锁失败，任务未执行: key={}", lockKey);
            return false;
        }
        
        try {
            task.run();
            return true;
        } finally {
            unlock(lockKey);
        }
    }

    /**
     * 使用分布式锁执行任务（带返回值）
     * @param lockKey 锁的key
     * @param leaseTime 锁的持有时间（秒）
     * @param supplier 要执行的任务
     * @return 任务结果，如果获取锁失败返回null
     */
    public <T> T executeWithLock(String lockKey, long leaseTime, java.util.function.Supplier<T> supplier) {
        if (!tryLock(lockKey, leaseTime)) {
            log.warn("获取锁失败，任务未执行: key={}", lockKey);
            return null;
        }
        
        try {
            return supplier.get();
        } finally {
            unlock(lockKey);
        }
    }
}
