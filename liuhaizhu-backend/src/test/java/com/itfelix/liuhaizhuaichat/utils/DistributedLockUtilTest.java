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

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DistributedLockUtil 单元测试
 * @author aceFelix
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DistributedLockUtil 单元测试")
class DistributedLockUtilTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private DistributedLockUtil distributedLockUtil;

    @Nested
    @DisplayName("tryLock - 获取锁")
    class TryLockTests {

        @Test
        @DisplayName("锁未被持有时应成功获取")
        void shouldAcquireLockWhenFree() {
            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                    .thenReturn(true);

            boolean result = distributedLockUtil.tryLock("test-lock", 10);

            assertTrue(result);
        }

        @Test
        @DisplayName("锁已被持有时应快速失败（默认等待0秒）")
        void shouldFailFastWhenLockHeld() {
            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                    .thenReturn(false);

            boolean result = distributedLockUtil.tryLock("busy-lock", 10);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("unlock - 释放锁")
    class UnlockTests {

        @Test
        @DisplayName("当前线程持有锁时应成功释放")
        void shouldUnlockWhenOwner() {
            String threadId = String.valueOf(Thread.currentThread().getId());
            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(anyString())).thenReturn(threadId);
            when(stringRedisTemplate.delete(anyString())).thenReturn(true);

            boolean result = distributedLockUtil.unlock("test-lock");

            assertTrue(result);
        }

        @Test
        @DisplayName("非锁持有者释放锁应失败")
        void shouldFailUnlockWhenNotOwner() {
            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(anyString())).thenReturn("other-thread-id");

            boolean result = distributedLockUtil.unlock("test-lock");

            assertFalse(result);
            verify(stringRedisTemplate, never()).delete(anyString());
        }
    }

    @Nested
    @DisplayName("isLocked - 检查锁")
    class IsLockedTests {

        @Test
        @DisplayName("锁存在时应返回 true")
        void shouldReturnTrueWhenLockExists() {
            when(stringRedisTemplate.hasKey(contains("lock:active-lock"))).thenReturn(true);

            assertTrue(distributedLockUtil.isLocked("active-lock"));
        }

        @Test
        @DisplayName("锁不存在时应返回 false")
        void shouldReturnFalseWhenLockNotExists() {
            when(stringRedisTemplate.hasKey(contains("lock:free-lock"))).thenReturn(false);

            assertFalse(distributedLockUtil.isLocked("free-lock"));
        }
    }

    @Nested
    @DisplayName("executeWithLock(Runnable) - 带锁执行无返回值任务")
    class ExecuteWithLockRunnableTests {

        @Test
        @DisplayName("获取锁成功后应执行任务并释放锁")
        void shouldExecuteTaskAndReleaseLock() {
            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                    .thenReturn(true);
            String threadId = String.valueOf(Thread.currentThread().getId());
            when(valueOperations.get(anyString())).thenReturn(threadId);
            when(stringRedisTemplate.delete(anyString())).thenReturn(true);

            boolean[] executed = {false};
            boolean result = distributedLockUtil.executeWithLock("task-lock", 10,
                    () -> executed[0] = true);

            assertTrue(result);
            assertTrue(executed[0]);
        }

        @Test
        @DisplayName("获取锁失败时不应执行任务")
        void shouldNotExecuteTaskWhenLockFailed() {
            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                    .thenReturn(false);

            boolean[] executed = {false};
            boolean result = distributedLockUtil.executeWithLock("busy-lock", 10,
                    (Runnable) () -> { executed[0] = true; });

            assertFalse(result);
            assertFalse(executed[0]);
        }
    }

    @Nested
    @DisplayName("executeWithLock(Supplier) - 带锁执行有返回值任务")
    class ExecuteWithLockSupplierTests {

        @Test
        @DisplayName("应正确返回 Supplier 的结果")
        void shouldReturnSupplierResult() {
            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                    .thenReturn(true);
            String threadId = String.valueOf(Thread.currentThread().getId());
            when(valueOperations.get(anyString())).thenReturn(threadId);
            when(stringRedisTemplate.delete(anyString())).thenReturn(true);

            String result = distributedLockUtil.executeWithLock("supplier-lock", 10,
                    () -> "task-result");

            assertEquals("task-result", result);
        }

        @Test
        @DisplayName("获取锁失败时应返回 null")
        void shouldReturnNullWhenLockFailed() {
            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                    .thenReturn(false);

            String result = distributedLockUtil.executeWithLock("busy-lock", 10,
                    () -> "should-not-return");

            assertNull(result);
        }
    }
}
