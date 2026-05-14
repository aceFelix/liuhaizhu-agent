package com.itfelix.liuhaizhuaichat.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的请求限流工具
 * 使用令牌桶算法实现
 * @author aceFelix
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimiterUtil {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String RATE_LIMITER_PREFIX = "rate_limiter:";

    /**
     * 令牌桶算法Lua脚本
     * KEYS[1]: 限流key
     * ARGV[1]: 令牌桶容量
     * ARGV[2]: 令牌生成速率（每秒生成的令牌数）
     * ARGV[3]: 当前时间戳（毫秒）
     * ARGV[4]: 请求的令牌数量（默认为1）
     * 返回: 1表示允许，0表示拒绝
     */
    private static final String TOKEN_BUCKET_SCRIPT = """
            local key = KEYS[1]
            local capacity = tonumber(ARGV[1])
            local rate = tonumber(ARGV[2])
            local now = tonumber(ARGV[3])
            local requested = tonumber(ARGV[4])
            
            -- 获取当前令牌桶状态
            local info = redis.call('hmget', key, 'tokens', 'last_time')
            local tokens = tonumber(info[1])
            local lastTime = tonumber(info[2])
            
            -- 如果桶不存在，初始化为满桶
            if tokens == nil then
                tokens = capacity
                lastTime = now
            end
            
            -- 计算需要补充的令牌数
            local delta = math.max(0, now - lastTime)
            local filledTokens = math.min(capacity, tokens + (delta * rate / 1000))
            
            -- 判断是否有足够的令牌
            if filledTokens >= requested then
                local newTokens = filledTokens - requested
                redis.call('hmset', key, 'tokens', newTokens, 'last_time', now)
                redis.call('pexpire', key, math.ceil(capacity / rate * 1000) + 1000)
                return 1
            else
                redis.call('hmset', key, 'tokens', filledTokens, 'last_time', now)
                redis.call('pexpire', key, math.ceil(capacity / rate * 1000) + 1000)
                return 0
            end
            """;

    /**
     * 尝试获取令牌
     * @param key 限流key
     * @param capacity 令牌桶容量
     * @param rate 令牌生成速率（每秒生成的令牌数）
     * @return 是否获取成功
     */
    public boolean tryAcquire(String key, int capacity, int rate) {
        return tryAcquire(key, capacity, rate, 1);
    }

    /**
     * 尝试获取指定数量的令牌
     * @param key 限流key
     * @param capacity 令牌桶容量
     * @param rate 令牌生成速率（每秒生成的令牌数）
     * @param permits 请求的令牌数量
     * @return 是否获取成功
     */
    public boolean tryAcquire(String key, int capacity, int rate, int permits) {
        String fullKey = RATE_LIMITER_PREFIX + key;
        long now = System.currentTimeMillis();

        DefaultRedisScript<Long> script = new DefaultRedisScript<>(TOKEN_BUCKET_SCRIPT, Long.class);
        Long result = stringRedisTemplate.execute(
                script,
                Collections.singletonList(fullKey),
                String.valueOf(capacity),
                String.valueOf(rate),
                String.valueOf(now),
                String.valueOf(permits)
        );

        boolean allowed = result != null && result == 1;
        if (!allowed) {
            log.warn("请求被限流: key={}, capacity={}, rate={}", fullKey, capacity, rate);
        }
        return allowed;
    }

    /**
     * 滑动窗口限流
     * @param key 限流key
     * @param limit 时间窗口内允许的最大请求数
     * @param windowSize 时间窗口大小（秒）
     * @return 是否允许通过
     */
    public boolean tryAcquireWithWindow(String key, int limit, int windowSize) {
        String fullKey = RATE_LIMITER_PREFIX + "window:" + key;
        long now = System.currentTimeMillis();
        long windowStart = now - windowSize * 1000L;

        // 移除窗口外的请求
        stringRedisTemplate.opsForZSet().removeRangeByScore(fullKey, 0, windowStart);

        // 获取当前窗口内的请求数
        Long count = stringRedisTemplate.opsForZSet().count(fullKey, windowStart, now);
        if (count == null) {
            count = 0L;
        }

        if (count >= limit) {
            log.warn("滑动窗口限流触发: key={}, count={}, limit={}", fullKey, count, limit);
            return false;
        }

        // 添加当前请求
        stringRedisTemplate.opsForZSet().add(fullKey, String.valueOf(now), now);
        stringRedisTemplate.expire(fullKey, windowSize, TimeUnit.SECONDS);
        return true;
    }

    /**
     * 固定窗口限流（简单计数器）
     * @param key 限流key
     * @param limit 时间窗口内允许的最大请求数
     * @param windowSize 时间窗口大小（秒）
     * @return 是否允许通过
     */
    public boolean tryAcquireWithFixedWindow(String key, int limit, int windowSize) {
        String fullKey = RATE_LIMITER_PREFIX + "fixed:" + key;
        
        Long count = stringRedisTemplate.opsForValue().increment(fullKey);
        if (count == null) {
            count = 1L;
        }
        
        if (count == 1) {
            stringRedisTemplate.expire(fullKey, windowSize, TimeUnit.SECONDS);
        }
        
        if (count > limit) {
            log.warn("固定窗口限流触发: key={}, count={}, limit={}", fullKey, count, limit);
            return false;
        }
        
        return true;
    }
}
