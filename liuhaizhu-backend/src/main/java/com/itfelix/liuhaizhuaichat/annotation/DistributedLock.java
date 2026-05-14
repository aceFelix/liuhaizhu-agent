package com.itfelix.liuhaizhuaichat.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁注解
 * 用于方法级别的分布式锁控制
 * @author aceFelix
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    
    /**
     * 锁的key，支持SpEL表达式
     * 例如: "#userId + ':' + #fileName"
     */
    String key();
    
    /**
     * 锁的持有时间（秒）
     */
    long leaseTime() default 30;
    
    /**
     * 等待获取锁的时间（秒），0表示不等待
     */
    long waitTime() default 0;
    
    /**
     * 获取锁失败时的提示信息
     */
    String message() default "操作正在进行中，请稍后重试";
}
