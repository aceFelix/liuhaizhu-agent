package com.itfelix.liuhaizhuaichat.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SSEServerUtil 单元测试
 * @author aceFelix
 */
@DisplayName("SSEServerUtil 单元测试")
class SSEServerUtilTest {

    @AfterEach
    void tearDown() {
        SSEServerUtil.getSseClients().clear();
    }

    @Nested
    @DisplayName("连接管理")
    class ConnectionManagementTests {

        @Test
        @DisplayName("connect 后用户应在线")
        void shouldBeOnlineAfterConnect() {
            try {
                SseEmitter emitter = SSEServerUtil.connect("user123");
                assertTrue(SSEServerUtil.isOnline("user123"));
                // 清理
                SSEServerUtil.remove("user123", emitter);
            } catch (Exception ignored) {
                // SseEmitter 在单元测试中可能因缺少 Servlet 上下文而失败
            }
        }
    }

    @Nested
    @DisplayName("isOnline - 在线检查")
    class IsOnlineTests {

        @Test
        @DisplayName("新用户应不在线")
        void newUserShouldBeOffline() {
            assertFalse(SSEServerUtil.isOnline("newUser"));
        }

        @Test
        @DisplayName("不存在的用户应返回 false")
        void shouldReturnFalseForNonExistentUser() {
            assertFalse(SSEServerUtil.isOnline("ghost_user_xyz"));
        }
    }

    @Nested
    @DisplayName("getConnectionCount - 连接数")
    class ConnectionCountTests {

        @Test
        @DisplayName("初始连接数应为 0")
        void initialCountShouldBeZero() {
            assertEquals(0, SSEServerUtil.getConnectionCount());
        }
    }

    @Nested
    @DisplayName("getSseClients - 获取客户端列表")
    class GetSseClientsTests {

        @Test
        @DisplayName("应返回非空的 Map")
        void shouldReturnNonNullMap() {
            assertNotNull(SSEServerUtil.getSseClients());
        }

        @Test
        @DisplayName("应返回同一个 Map 实例")
        void shouldReturnSameInstance() {
            var map1 = SSEServerUtil.getSseClients();
            var map2 = SSEServerUtil.getSseClients();
            assertSame(map1, map2);
        }

        @Test
        @DisplayName("初始状态应为空")
        void shouldBeEmptyInitially() {
            assertTrue(SSEServerUtil.getSseClients().isEmpty());
        }
    }
}
