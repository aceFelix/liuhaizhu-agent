package com.itfelix.liuhaizhuaichat.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RateLimiterUtil 单元测试
 * @author aceFelix
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimiterUtil 单元测试")
class RateLimiterUtilTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private RateLimiterUtil rateLimiterUtil;

    @Nested
    @DisplayName("tryAcquire - 令牌桶限流")
    class TryAcquireTests {

        @Test
        @DisplayName("令牌充足时应返回 true")
        void shouldReturnTrueWhenTokensAvailable() {
            when(stringRedisTemplate.execute(any(DefaultRedisScript.class),
                    anyList(), anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(1L);

            boolean result = rateLimiterUtil.tryAcquire("api:test", 10, 5);

            assertTrue(result);
        }

        @Test
        @DisplayName("令牌不足时应返回 false")
        void shouldReturnFalseWhenTokensExhausted() {
            when(stringRedisTemplate.execute(any(DefaultRedisScript.class),
                    anyList(), anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(0L);

            boolean result = rateLimiterUtil.tryAcquire("api:test", 10, 5);

            assertFalse(result);
        }

        @Test
        @DisplayName("Redis 返回 null 时应返回 false")
        void shouldReturnFalseWhenRedisReturnsNull() {
            when(stringRedisTemplate.execute(any(DefaultRedisScript.class),
                    anyList(), anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(null);

            boolean result = rateLimiterUtil.tryAcquire("api:test", 10, 5);

            assertFalse(result);
        }

        @Test
        @DisplayName("自定义 permits 参数应正确传递")
        void shouldSupportCustomPermits() {
            when(stringRedisTemplate.execute(any(DefaultRedisScript.class),
                    anyList(), anyString(), anyString(), anyString(), eq("3")))
                    .thenReturn(1L);

            boolean result = rateLimiterUtil.tryAcquire("api:bulk", 10, 5, 3);

            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("tryAcquireWithFixedWindow - 固定窗口限流")
    class TryAcquireWithFixedWindowTests {

        @Test
        @DisplayName("窗口内计数未超限时应返回 true")
        void shouldReturnTrueWithinLimit() {
            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.increment(anyString())).thenReturn(1L);

            boolean result = rateLimiterUtil.tryAcquireWithFixedWindow("api:fixed", 100, 60);

            assertTrue(result);
        }

        @Test
        @DisplayName("窗口内计数超限时应返回 false")
        void shouldReturnFalseWhenExceeded() {
            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.increment(anyString())).thenReturn(101L);

            boolean result = rateLimiterUtil.tryAcquireWithFixedWindow("api:fixed", 100, 60);

            assertFalse(result);
        }

        @Test
        @DisplayName("首次请求应设置过期时间")
        void shouldSetExpireOnFirstRequest() {
            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.increment(anyString())).thenReturn(1L);

            rateLimiterUtil.tryAcquireWithFixedWindow("api:fixed", 100, 60);

            verify(stringRedisTemplate).expire(anyString(), eq(60L), eq(TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("非首次请求不应设置过期时间")
        void shouldNotSetExpireOnSubsequentRequests() {
            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.increment(anyString())).thenReturn(5L);

            rateLimiterUtil.tryAcquireWithFixedWindow("api:fixed", 100, 60);

            verify(stringRedisTemplate, never()).expire(anyString(), anyLong(), any(TimeUnit.class));
        }
    }

    @Nested
    @DisplayName("tryAcquireWithWindow - 滑动窗口限流")
    class TryAcquireWithWindowTests {

        @Test
        @DisplayName("窗口内请求数未超限时应返回 true")
        void shouldReturnTrueWhenWithinSlidingWindow() {
            when(stringRedisTemplate.opsForZSet()).thenReturn(zSetOperations);
            when(zSetOperations.count(anyString(), anyDouble(), anyDouble())).thenReturn(5L);

            boolean result = rateLimiterUtil.tryAcquireWithWindow("api:slide", 10, 60);

            assertTrue(result);
            verify(zSetOperations).add(anyString(), anyString(), anyDouble());
        }

        @Test
        @DisplayName("窗口内请求数超限时应返回 false")
        void shouldReturnFalseWhenSlidingWindowExceeded() {
            when(stringRedisTemplate.opsForZSet()).thenReturn(zSetOperations);
            when(zSetOperations.count(anyString(), anyDouble(), anyDouble())).thenReturn(11L);

            boolean result = rateLimiterUtil.tryAcquireWithWindow("api:slide", 10, 60);

            assertFalse(result);
            verify(zSetOperations, never()).add(anyString(), anyString(), anyDouble());
        }

        @Test
        @DisplayName("count 为 null 时应视为 0")
        void shouldTreatNullCountAsZero() {
            when(stringRedisTemplate.opsForZSet()).thenReturn(zSetOperations);
            when(zSetOperations.count(anyString(), anyDouble(), anyDouble())).thenReturn(null);

            boolean result = rateLimiterUtil.tryAcquireWithWindow("api:slide", 10, 60);

            assertTrue(result);
        }
    }
}
