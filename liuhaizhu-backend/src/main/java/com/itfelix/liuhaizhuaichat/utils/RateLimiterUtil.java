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
     * 滑动窗口限流 Lua 脚本（桶计数方案）
     *
     * 设计思路：
     * - 将时间窗口等分为 N 个桶，每个桶只存计数（不是逐条请求记录）
     * - 内存 O(buckets) 常数级，与 QPS 无关，10万QPS 也只用几百字节
     * - 单次 Lua 原子执行，消除并发竞态
     *
     * Redis Hash 结构：
     *   cnt_0 ~ cnt_{N-1} : 各桶的请求计数
     *   ts_0  ~ ts_{N-1}  : 各桶最近一次写入的时间戳（用于判断桶是否过期）
     *
     * KEYS[1]: 限流key
     * ARGV[1]: 窗口大小（毫秒）
     * ARGV[2]: 桶数量
     * ARGV[3]: 限流上限
     * ARGV[4]: 当前时间戳（毫秒）
     */
    private static final String SLIDING_WINDOW_SCRIPT = """
            local key         = KEYS[1]
            local window_ms   = tonumber(ARGV[1])
            local buckets     = tonumber(ARGV[2])
            local limit       = tonumber(ARGV[3])
            local now         = tonumber(ARGV[4])

            local bucket_span      = window_ms / buckets
            local current_idx      = math.floor((now % window_ms) / bucket_span)
            local current_window   = math.floor(now / window_ms)

            -- 1. 遍历所有桶，剔除过期桶，累加有效计数
            local total = 0
            for i = 0, buckets - 1 do
                local ts  = tonumber(redis.call('HGET', key, 'ts_' .. i)) or 0
                local cnt = tonumber(redis.call('HGET', key, 'cnt_' .. i)) or 0

                local bucket_window = math.floor(ts / window_ms)

                -- 判断桶是否在当前滑动窗口内
                local valid = false
                if bucket_window == current_window and i <= current_idx then
                    valid = true   -- 当前窗口内，且桶位置不超当前指针
                elseif bucket_window == (current_window - 1) and i > current_idx then
                    valid = true   -- 上一窗口遗留下来的桶，位置在当前指针之后
                end

                if valid then
                    total = total + cnt
                end
            end

            -- 2. 超限直接拒绝
            if total >= limit then
                return 0
            end

            -- 3. 当前桶计数 +1，更新时间戳
            redis.call('HINCRBY', key, 'cnt_' .. current_idx, 1)
            redis.call('HSET', key, 'ts_' .. current_idx, now)

            -- 4. 设置过期：窗口两倍足够覆盖所有桶
            redis.call('PEXPIRE', key, window_ms * 2)

            return 1
            """;

    /**
     * 滑动窗口限流（桶计数方案）
     * <p>
     * 将时间窗口等分为 N 个桶，每桶只记次数，不逐条存请求。
     * 内存占用 = O(桶数量)，与 QPS 无关。单次 Lua 原子执行。
     *
     * @param key        限流key
     * @param limit      时间窗口内允许的最大请求数
     * @param windowSize 时间窗口大小（秒）
     * @return 是否允许通过
     */
    public boolean tryAcquireWithWindow(String key, int limit, int windowSize) {
        return tryAcquireWithWindow(key, limit, windowSize, 10);
    }

    /**
     * 滑动窗口限流（可指定桶数量）
     *
     * @param key        限流key
     * @param limit      时间窗口内允许的最大请求数
     * @param windowSize 时间窗口大小（秒）
     * @param buckets    桶数量（窗口内精度，默认10，即窗口 / 10 的粒度）
     * @return 是否允许通过
     */
    public boolean tryAcquireWithWindow(String key, int limit, int windowSize, int buckets) {
        String fullKey = RATE_LIMITER_PREFIX + "window:" + key;
        long now = System.currentTimeMillis();
        long windowMs = windowSize * 1000L;

        DefaultRedisScript<Long> script = new DefaultRedisScript<>(SLIDING_WINDOW_SCRIPT, Long.class);
        Long result = stringRedisTemplate.execute(
                script,
                Collections.singletonList(fullKey),
                String.valueOf(windowMs),
                String.valueOf(buckets),
                String.valueOf(limit),
                String.valueOf(now)
        );

        boolean allowed = result != null && result == 1;
        if (!allowed) {
            log.warn("滑动窗口限流触发: key={}, limit={}, window={}s", fullKey, limit, windowSize);
        }
        return allowed;
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
