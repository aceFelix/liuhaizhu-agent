package com.itfelix.liuhaizhuaichat.security;

import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;
import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import com.itfelix.liuhaizhuaichat.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JwtAuthenticationFilter 单元测试
 * @author aceFelix
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter 单元测试")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private com.itfelix.liuhaizhuaichat.mapper.UserMapper userMapper;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        filter = new JwtAuthenticationFilter(jwtUtil, userMapper);
    }

    @Nested
    @DisplayName("extractTokenFromRequest - 从请求提取Token")
    class ExtractTokenTests {

        @Test
        @DisplayName("有效的 Bearer Token 应正确提取")
        void shouldExtractBearerToken() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            FilterChain chain = mock(FilterChain.class);

            when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token.here");
            when(request.getRequestURI()).thenReturn("/api/test");
            when(request.getMethod()).thenReturn("GET");
            when(jwtUtil.validateAccessToken("valid.jwt.token.here")).thenReturn(true);
            when(jwtUtil.getUserIdFromToken("valid.jwt.token.here")).thenReturn("user123");

            User user = createTestUser();
            when(userMapper.selectById("user123")).thenReturn(user);

            filter.doFilterInternal(request, response, chain);

            verify(chain).doFilter(request, response);
            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("无 Authorization 头时不应设置认证")
        void shouldNotAuthenticateWithoutAuthHeader() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            FilterChain chain = mock(FilterChain.class);

            when(request.getHeader("Authorization")).thenReturn(null);
            when(request.getRequestURI()).thenReturn("/api/test");
            when(request.getMethod()).thenReturn("GET");

            filter.doFilterInternal(request, response, chain);

            verify(chain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("无效的 Bearer Token 不应设置认证")
        void shouldNotAuthenticateWithInvalidToken() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            FilterChain chain = mock(FilterChain.class);

            when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token.here");
            when(request.getRequestURI()).thenReturn("/api/test");
            when(request.getMethod()).thenReturn("GET");
            when(jwtUtil.validateAccessToken("invalid.token.here")).thenReturn(false);

            filter.doFilterInternal(request, response, chain);

            verify(chain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Token 有效但用户不存在时不应设置认证")
        void shouldNotAuthenticateWhenUserNotFound() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            FilterChain chain = mock(FilterChain.class);

            when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
            when(request.getRequestURI()).thenReturn("/api/test");
            when(request.getMethod()).thenReturn("GET");
            when(jwtUtil.validateAccessToken("valid.jwt.token")).thenReturn(true);
            when(jwtUtil.getUserIdFromToken("valid.jwt.token")).thenReturn("ghost");
            when(userMapper.selectById("ghost")).thenReturn(null);

            filter.doFilterInternal(request, response, chain);

            verify(chain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Authorization 头不以 Bearer 开头时应忽略")
        void shouldIgnoreNonBearerToken() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            FilterChain chain = mock(FilterChain.class);

            when(request.getHeader("Authorization")).thenReturn("Basic abc123");
            when(request.getRequestURI()).thenReturn("/api/test");
            when(request.getMethod()).thenReturn("GET");

            filter.doFilterInternal(request, response, chain);

            verify(chain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("VIP 用户应有正确的认证角色")
        void shouldSetCorrectRoleForVipUser() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            FilterChain chain = mock(FilterChain.class);

            when(request.getHeader("Authorization")).thenReturn("Bearer vip.jwt.token");
            when(request.getRequestURI()).thenReturn("/api/admin");
            when(request.getMethod()).thenReturn("POST");
            when(jwtUtil.validateAccessToken("vip.jwt.token")).thenReturn(true);
            when(jwtUtil.getUserIdFromToken("vip.jwt.token")).thenReturn("vip456");

            User vipUser = createTestUser("vip456", "vipUser", UserRoleEnum.VIP);
            when(userMapper.selectById("vip456")).thenReturn(vipUser);

            filter.doFilterInternal(request, response, chain);

            var auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth);
            assertTrue(auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_VIP")));
        }
    }

    private User createTestUser() {
        return createTestUser("user123", "testUser", UserRoleEnum.USER);
    }

    private User createTestUser(String id, String username, UserRoleEnum role) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(username);
        user.setPassword("encoded_pass");
        user.setRole(role);
        user.setStatus(1);
        return user;
    }
}
