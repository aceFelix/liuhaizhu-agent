package com.itfelix.liuhaizhuaichat.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求限流注解
 * 支持令牌桶和滑动窗口两种限流策略
 * @author aceFelix
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    
    /**
     * 限流key，支持SpEL表达式
     * 例如: "'chat:' + #userId"
     * 如果不指定，默认使用方法名
     */
    String key() default "";
    
    /**
     * 限流策略
     */
    LimitType type() default LimitType.TOKEN_BUCKET;
    
    /**
     * 令牌桶容量 / 时间窗口内允许的最大请求数
     */
    int capacity() default 10;
    
    /**
     * 令牌生成速率（每秒生成的令牌数）/ 时间窗口大小（秒）
     */
    int rate() default 5;
    
    /**
     * 被限流时的提示信息
     */
    String message() default "请求过于频繁，请稍后重试";
    
    /**
     * 限流策略类型
     */
    enum LimitType {
        TOKEN_BUCKET,
        SLIDING_WINDOW,
        FIXED_WINDOW
    }
}
