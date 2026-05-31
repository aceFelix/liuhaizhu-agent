package com.itfelix.liuhaizhuaichat.service.impl;

import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;
import com.itfelix.liuhaizhuaichat.pojo.dto.LoginRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.RegisterRequest;
import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import com.itfelix.liuhaizhuaichat.pojo.vo.LoginResponse;
import com.itfelix.liuhaizhuaichat.pojo.vo.UserVO;
import com.itfelix.liuhaizhuaichat.service.AdminUserService;
import com.itfelix.liuhaizhuaichat.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthServiceImpl 单元测试
 * @author aceFelix
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl 单元测试")
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AdminUserService adminUserService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "fromEmail", "test@example.com");
    }

    private User createTestUser(String id, String username, UserRoleEnum role) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(username);
        user.setPassword("encoded_password");
        user.setEmail(username + "@example.com");
        user.setRole(role);
        user.setStatus(1);
        user.setAvatar("/images/user-avatar.jpg");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return user;
    }

    @Nested
    @DisplayName("login - 登录")
    class LoginTests {

        @Test
        @DisplayName("正确的用户名密码应成功登录并返回 token")
        void shouldLoginSuccessfullyWithCorrectCredentials() {
            LoginRequest request = new LoginRequest();
            request.setUsername("testUser");
            request.setPassword("password123");

            User user = createTestUser("user123", "testUser", UserRoleEnum.USER);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mock(Authentication.class));
            when(adminUserService.findByUsername("testUser")).thenReturn(user);
            when(jwtUtil.generateToken("user123", "testUser", "USER")).thenReturn("access.jwt.token");
            when(jwtUtil.generateRefreshToken("user123")).thenReturn("refresh.jwt.token");
            when(adminUserService.convertToVO(user)).thenReturn(new UserVO());

            LoginResponse response = authService.login(request);

            assertNotNull(response);
            assertEquals("access.jwt.token", response.getToken());
            assertEquals("refresh.jwt.token", response.getRefreshToken());
            assertNotNull(response.getUser());
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }

        @Test
        @DisplayName("密码错误应抛出异常")
        void shouldThrowWhenPasswordWrong() {
            LoginRequest request = new LoginRequest();
            request.setUsername("testUser");
            request.setPassword("wrongPassword");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
            assertEquals("用户名或密码错误", ex.getMessage());
            verify(adminUserService, never()).findByUsername(anyString());
        }

        @Test
        @DisplayName("认证通过但用户不存在应抛出异常")
        void shouldThrowWhenUserNotFoundAfterAuth() {
            LoginRequest request = new LoginRequest();
            request.setUsername("ghostUser");
            request.setPassword("password123");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mock(Authentication.class));
            when(adminUserService.findByUsername("ghostUser")).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
            assertEquals("用户不存在", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("register - 注册")
    class RegisterTests {

        @Test
        @DisplayName("用户名注册成功应返回登录响应")
        void shouldRegisterSuccessfullyWithUsername() {
            RegisterRequest request = new RegisterRequest();
            request.setUsername("newUser");
            request.setPassword("password123");
            request.setRegisterMode("username");
            request.setEmail("");

            when(adminUserService.findByUsername("newUser")).thenReturn(null);
            User user = createTestUser("user456", "newUser", UserRoleEnum.USER);
            when(adminUserService.register(request)).thenReturn(user);
            when(jwtUtil.generateToken("user456", "newUser", "USER")).thenReturn("access.jwt.token");
            when(jwtUtil.generateRefreshToken("user456")).thenReturn("refresh.jwt.token");
            when(adminUserService.convertToVO(user)).thenReturn(new UserVO());

            LoginResponse response = authService.register(request);

            assertNotNull(response);
            assertEquals("access.jwt.token", response.getToken());
            assertEquals("refresh.jwt.token", response.getRefreshToken());
            verify(adminUserService).register(request);
        }

        @Test
        @DisplayName("用户名已存在应抛出异常")
        void shouldThrowWhenUsernameExists() {
            RegisterRequest request = new RegisterRequest();
            request.setUsername("existingUser");
            request.setPassword("password123");
            request.setRegisterMode("username");

            when(adminUserService.findByUsername("existingUser"))
                    .thenReturn(createTestUser("user001", "existingUser", UserRoleEnum.USER));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(request));
            assertEquals("用户名已被注册", ex.getMessage());
            verify(adminUserService, never()).register(any());
        }

        @Test
        @DisplayName("无效的注册方式应抛出异常")
        void shouldThrowForInvalidRegisterMode() {
            RegisterRequest request = new RegisterRequest();
            request.setUsername("newUser");
            request.setPassword("password123");
            request.setRegisterMode("wechat");

            when(adminUserService.findByUsername("newUser")).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(request));
            assertEquals("无效的注册方式", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("refreshToken - 刷新 Token")
    class RefreshTokenTests {

        @Test
        @DisplayName("有效的 refreshToken 应返回新的 token 对")
        void shouldRefreshSuccessfullyWithValidToken() {
            when(jwtUtil.validateRefreshToken("valid.refresh.token")).thenReturn(true);
            when(jwtUtil.getUserIdFromToken("valid.refresh.token")).thenReturn("user123");
            User user = createTestUser("user123", "testUser", UserRoleEnum.USER);
            when(adminUserService.findById("user123")).thenReturn(user);
            when(jwtUtil.generateToken("user123", "testUser", "USER")).thenReturn("new.access.jwt");
            when(jwtUtil.generateRefreshToken("user123")).thenReturn("new.refresh.jwt");
            when(adminUserService.convertToVO(user)).thenReturn(new UserVO());

            LoginResponse response = authService.refreshToken("valid.refresh.token");

            assertNotNull(response);
            assertEquals("new.access.jwt", response.getToken());
            assertEquals("new.refresh.jwt", response.getRefreshToken());
        }

        @Test
        @DisplayName("无效的 refreshToken 应抛出异常")
        void shouldThrowForInvalidRefreshToken() {
            when(jwtUtil.validateRefreshToken("invalid.token")).thenReturn(false);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.refreshToken("invalid.token"));
            assertEquals("刷新token无效或已过期", ex.getMessage());
        }

        @Test
        @DisplayName("refreshToken 有效但用户已被删除应抛出异常")
        void shouldThrowWhenUserNotFound() {
            when(jwtUtil.validateRefreshToken("valid.orphan.token")).thenReturn(true);
            when(jwtUtil.getUserIdFromToken("valid.orphan.token")).thenReturn("deletedUser");
            when(adminUserService.findById("deletedUser")).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.refreshToken("valid.orphan.token"));
            assertEquals("用户不存在", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("getCurrentUser - 获取当前用户")
    class GetCurrentUserTests {

        @Test
        @DisplayName("应返回用户信息")
        void shouldReturnCurrentUserInfo() {
            User user = createTestUser("user123", "testUser", UserRoleEnum.USER);
            UserVO userVO = new UserVO();
            userVO.setUserId("user123");
            userVO.setUsername("testUser");

            when(adminUserService.findByUsername("testUser")).thenReturn(user);
            when(adminUserService.convertToVO(user)).thenReturn(userVO);

            UserVO result = authService.getCurrentUser("testUser");

            assertEquals("user123", result.getUserId());
            assertEquals("testUser", result.getUsername());
        }

        @Test
        @DisplayName("username 为 null 应抛出异常")
        void shouldThrowWhenUsernameIsNull() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.getCurrentUser(null));
            assertEquals("未登录", ex.getMessage());
        }

        @Test
        @DisplayName("用户不存在应抛出异常")
        void shouldThrowWhenUserNotFound() {
            when(adminUserService.findByUsername("ghost")).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.getCurrentUser("ghost"));
            assertEquals("用户不存在", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("getCurrentUserById - 通过ID获取当前用户")
    class GetCurrentUserByIdTests {

        @Test
        @DisplayName("userId 为 null 应抛出异常")
        void shouldThrowWhenUserIdIsNull() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.getCurrentUserById(null));
            assertEquals("未登录", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("deleteAccount - 注销账户")
    class DeleteAccountTests {

        @Test
        @DisplayName("应成功注销用户")
        void shouldDeleteAccountSuccessfully() {
            User user = createTestUser("user123", "testUser", UserRoleEnum.USER);
            when(adminUserService.findByUsername("testUser")).thenReturn(user);
            doNothing().when(adminUserService).deleteUser("user123");

            assertDoesNotThrow(() -> authService.deleteAccount("testUser"));
            verify(adminUserService).deleteUser("user123");
        }

        @Test
        @DisplayName("username 为 null 应抛出异常")
        void shouldThrowWhenUsernameIsNullForDelete() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.deleteAccount(null));
            assertEquals("未登录", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("upgradeToVip - 升级VIP")
    class UpgradeToVipTests {

        @Test
        @DisplayName("普通用户应成功升级为 VIP")
        void shouldUpgradeNormalUserToVip() {
            User user = createTestUser("user123", "normalUser", UserRoleEnum.USER);
            when(adminUserService.findById("user123")).thenReturn(user);
            doNothing().when(adminUserService).updateUserRole("user123", "VIP");

            assertDoesNotThrow(() -> authService.upgradeToVip("user123"));
            verify(adminUserService).updateUserRole("user123", "VIP");
        }

        @Test
        @DisplayName("已是 VIP 的用户应抛出异常")
        void shouldThrowWhenAlreadyVip() {
            User vipUser = createTestUser("vip456", "vipUser", UserRoleEnum.VIP);
            when(adminUserService.findById("vip456")).thenReturn(vipUser);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.upgradeToVip("vip456"));
            assertEquals("您已经是VIP会员", ex.getMessage());
        }

        @Test
        @DisplayName("管理员应抛出异常（已是 VIP 以上）")
        void shouldThrowWhenAdmin() {
            User admin = createTestUser("admin789", "admin", UserRoleEnum.ADMIN);
            when(adminUserService.findById("admin789")).thenReturn(admin);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.upgradeToVip("admin789"));
            assertEquals("您已经是VIP会员", ex.getMessage());
        }

        @Test
        @DisplayName("userId 为 null 应抛出异常")
        void shouldThrowWhenUserIdIsNullForVip() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.upgradeToVip(null));
            assertEquals("未登录", ex.getMessage());
        }
    }
}
