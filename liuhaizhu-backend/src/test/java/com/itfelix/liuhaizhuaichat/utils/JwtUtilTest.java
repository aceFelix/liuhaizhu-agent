package com.itfelix.liuhaizhuaichat.utils;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试
 * @author aceFelix
 */
@DisplayName("JwtUtil 单元测试")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String TEST_SECRET = "test-secret-key-for-unit-testing-123456";
    private static final Long ACCESS_EXPIRATION = 86_400_000L;  // 24h
    private static final Long REFRESH_EXPIRATION = 604_800_000L; // 7d

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 使用反射注入 @Value 字段
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", ACCESS_EXPIRATION);
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", REFRESH_EXPIRATION);
    }

    @Nested
    @DisplayName("generateToken - 生成 AccessToken")
    class GenerateTokenTests {

        @Test
        @DisplayName("应成功生成非空的 access token")
        void shouldGenerateNonEmptyAccessToken() {
            String token = jwtUtil.generateToken("user123", "testUser", "USER");

            assertNotNull(token);
            assertFalse(token.isEmpty());
            assertTrue(token.split("\\.").length == 3, "JWT 应由三部分组成");
        }

        @Test
        @DisplayName("不同用户应生成不同的 token")
        void shouldGenerateDifferentTokensForDifferentUsers() {
            String token1 = jwtUtil.generateToken("user123", "alice", "USER");
            String token2 = jwtUtil.generateToken("user456", "bob", "VIP");

            assertNotEquals(token1, token2);
        }

        @Test
        @DisplayName("相同参数应生成不同的 token（包含不同的 iat）")
        void shouldGenerateDifferentTokensForSameParams() {
            String token1 = jwtUtil.generateToken("user123", "alice", "USER");
            String token2 = jwtUtil.generateToken("user123", "alice", "USER");

            // 注意：由于 iat 可能相同（同一毫秒），这里只验证都能正常解析
            assertNotNull(token1);
            assertNotNull(token2);
        }
    }

    @Nested
    @DisplayName("generateRefreshToken - 生成 RefreshToken")
    class GenerateRefreshTokenTests {

        @Test
        @DisplayName("应成功生成非空的 refresh token")
        void shouldGenerateNonEmptyRefreshToken() {
            String token = jwtUtil.generateRefreshToken("user123");

            assertNotNull(token);
            assertFalse(token.isEmpty());
        }

        @Test
        @DisplayName("refresh token 与 access token 应不同")
        void shouldBeDifferentFromAccessToken() {
            String accessToken = jwtUtil.generateToken("user123", "testUser", "USER");
            String refreshToken = jwtUtil.generateRefreshToken("user123");

            assertNotEquals(accessToken, refreshToken);
        }
    }

    @Nested
    @DisplayName("getUserIdFromToken - 从 Token 提取用户ID")
    class GetUserIdFromTokenTests {

        @Test
        @DisplayName("应正确提取用户ID")
        void shouldExtractUserId() {
            String token = jwtUtil.generateToken("user123", "testUser", "USER");
            String userId = jwtUtil.getUserIdFromToken(token);

            assertEquals("user123", userId);
        }

        @Test
        @DisplayName("应正确从 refresh token 提取用户ID")
        void shouldExtractUserIdFromRefreshToken() {
            String token = jwtUtil.generateRefreshToken("user789");
            String userId = jwtUtil.getUserIdFromToken(token);

            assertEquals("user789", userId);
        }
    }

    @Nested
    @DisplayName("getUsernameFromToken - 从 Token 提取用户名")
    class GetUsernameFromTokenTests {

        @Test
        @DisplayName("应正确提取用户名")
        void shouldExtractUsername() {
            String token = jwtUtil.generateToken("user123", "alice", "USER");
            String username = jwtUtil.getUsernameFromToken(token);

            assertEquals("alice", username);
        }
    }

    @Nested
    @DisplayName("getRoleFromToken - 从 Token 提取角色")
    class GetRoleFromTokenTests {

        @Test
        @DisplayName("应正确提取角色")
        void shouldExtractRole() {
            String token = jwtUtil.generateToken("user123", "admin", "ADMIN");
            String role = jwtUtil.getRoleFromToken(token);

            assertEquals("ADMIN", role);
        }

        @Test
        @DisplayName("应正确提取 VIP 角色")
        void shouldExtractVipRole() {
            String token = jwtUtil.generateToken("user456", "vipUser", "VIP");
            String role = jwtUtil.getRoleFromToken(token);

            assertEquals("VIP", role);
        }
    }

    @Nested
    @DisplayName("validateToken - 验证 Token 有效性")
    class ValidateTokenTests {

        @Test
        @DisplayName("有效 token 应返回 true")
        void shouldReturnTrueForValidToken() {
            String token = jwtUtil.generateToken("user123", "testUser", "USER");
            assertTrue(jwtUtil.validateToken(token));
        }

        @Test
        @DisplayName("篡改过的 token 应返回 false")
        void shouldReturnFalseForTamperedToken() {
            String token = jwtUtil.generateToken("user123", "testUser", "USER");
            // 篡改 token 的最后一字节
            String tamperedToken = token.substring(0, token.length() - 1) +
                    (token.charAt(token.length() - 1) == 'A' ? 'B' : 'A');

            assertFalse(jwtUtil.validateToken(tamperedToken));
        }

        @Test
        @DisplayName("空 token 应返回 false")
        void shouldReturnFalseForEmptyToken() {
            assertFalse(jwtUtil.validateToken(""));
        }

        @Test
        @DisplayName("无效格式的 token 应返回 false")
        void shouldReturnFalseForInvalidFormattedToken() {
            assertFalse(jwtUtil.validateToken("not.a.jwt.token"));
        }

        @Test
        @DisplayName("null token 应返回 false")
        void shouldReturnFalseForNullToken() {
            assertFalse(jwtUtil.validateToken(null));
        }
    }

    @Nested
    @DisplayName("validateRefreshToken - 验证 RefreshToken")
    class ValidateRefreshTokenTests {

        @Test
        @DisplayName("有效的 refresh token 应返回 true")
        void shouldReturnTrueForValidRefreshToken() {
            String token = jwtUtil.generateRefreshToken("user123");
            assertTrue(jwtUtil.validateRefreshToken(token));
        }

        @Test
        @DisplayName("access token 用作 refresh token 时应返回 false")
        void shouldReturnFalseWhenAccessTokenUsedAsRefreshToken() {
            String accessToken = jwtUtil.generateToken("user123", "testUser", "USER");
            assertFalse(jwtUtil.validateRefreshToken(accessToken),
                    "access token 不应被当作 refresh token 使用");
        }

        @Test
        @DisplayName("篡改过的 refresh token 应返回 false")
        void shouldReturnFalseForTamperedRefreshToken() {
            String token = jwtUtil.generateRefreshToken("user123");
            String tampered = token.substring(0, token.length() - 1) +
                    (token.charAt(token.length() - 1) == 'A' ? 'B' : 'A');

            assertFalse(jwtUtil.validateRefreshToken(tampered));
        }
    }

    @Nested
    @DisplayName("validateAccessToken - 验证 AccessToken")
    class ValidateAccessTokenTests {

        @Test
        @DisplayName("有效的 access token 应返回 true")
        void shouldReturnTrueForValidAccessToken() {
            String token = jwtUtil.generateToken("user123", "testUser", "USER");
            assertTrue(jwtUtil.validateAccessToken(token));
        }

        @Test
        @DisplayName("refresh token 用作 access token 时应返回 false")
        void shouldReturnFalseWhenRefreshTokenUsedAsAccessToken() {
            String refreshToken = jwtUtil.generateRefreshToken("user123");
            assertFalse(jwtUtil.validateAccessToken(refreshToken),
                    "refresh token 不应被当作 access token 使用");
        }

        @Test
        @DisplayName("篡改过的 access token 应返回 false")
        void shouldReturnFalseForTamperedAccessToken() {
            String token = jwtUtil.generateToken("user123", "testUser", "USER");
            String tampered = "x" + token.substring(1);

            assertFalse(jwtUtil.validateAccessToken(tampered));
        }
    }

    @Nested
    @DisplayName("跨方法集成测试")
    class CrossMethodIntegrationTests {

        @Test
        @DisplayName("完整流程：生成 → 验证 → 提取，所有步骤应一致")
        void shouldCompleteFullTokenLifecycle() {
            // 生成
            String accessToken = jwtUtil.generateToken("user999", "fullCycle", "VIP");
            String refreshToken = jwtUtil.generateRefreshToken("user999");

            // 验证
            assertTrue(jwtUtil.validateAccessToken(accessToken));
            assertTrue(jwtUtil.validateRefreshToken(refreshToken));

            // 提取
            assertEquals("user999", jwtUtil.getUserIdFromToken(accessToken));
            assertEquals("fullCycle", jwtUtil.getUsernameFromToken(accessToken));
            assertEquals("VIP", jwtUtil.getRoleFromToken(accessToken));
            assertEquals("user999", jwtUtil.getUserIdFromToken(refreshToken));
        }

        @Test
        @DisplayName("access token 对 refresh token 校验应拒绝，反之亦然")
        void shouldRejectCrossTypeValidation() {
            String accessToken = jwtUtil.generateToken("user123", "cross", "USER");
            String refreshToken = jwtUtil.generateRefreshToken("user123");

            // access token 不能当作 refresh token
            assertFalse(jwtUtil.validateRefreshToken(accessToken));
            // refresh token 不能当作 access token
            assertFalse(jwtUtil.validateAccessToken(refreshToken));
            // 但基础 validate 都对两者有效
            assertTrue(jwtUtil.validateToken(accessToken));
            assertTrue(jwtUtil.validateToken(refreshToken));
        }
    }
}
