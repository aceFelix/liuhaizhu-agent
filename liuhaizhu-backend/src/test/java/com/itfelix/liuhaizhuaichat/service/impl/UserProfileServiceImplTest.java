package com.itfelix.liuhaizhuaichat.service.impl;

import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;
import com.itfelix.liuhaizhuaichat.mapper.ChatMessageMapper;
import com.itfelix.liuhaizhuaichat.mapper.ConversationMapper;
import com.itfelix.liuhaizhuaichat.mapper.UserMapper;
import com.itfelix.liuhaizhuaichat.pojo.dto.BindEmailRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.UpdatePasswordRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.UpdateProfileRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.VerificationCodeRequest;
import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import com.itfelix.liuhaizhuaichat.pojo.vo.UserVO;
import com.itfelix.liuhaizhuaichat.service.RAGService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserProfileServiceImpl 单元测试
 * @author aceFelix
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserProfileServiceImpl 单元测试")
class UserProfileServiceImplTest {

    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ConversationMapper conversationMapper;
    @Mock private ChatMessageMapper chatMessageMapper;
    @Mock private RAGService ragService;
    @Mock private StringRedisTemplate stringRedisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;
    @Mock private JavaMailSender mailSender;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private static final String USER_ID = "user123";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userProfileService, "fromEmail", "noreply@test.com");
    }

    private User createTestUser() {
        User user = new User();
        user.setUserId(USER_ID);
        user.setUsername("testUser");
        user.setPassword("encoded_pass");
        user.setEmail("test@example.com");
        user.setRole(UserRoleEnum.USER);
        user.setStatus(1);
        user.setAvatar("/images/avatar.jpg");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return user;
    }

    @Nested
    @DisplayName("getCurrentUser - 获取当前用户")
    class GetCurrentUserTests {

        @Test
        @DisplayName("用户存在时应返回 VO")
        void shouldReturnUserVO() {
            User user = createTestUser();
            when(userMapper.selectById(USER_ID)).thenReturn(user);

            UserVO result = userProfileService.getCurrentUser(USER_ID);

            assertNotNull(result);
            assertEquals("testUser", result.getUsername());
        }

        @Test
        @DisplayName("用户不存在时应抛出异常")
        void shouldThrowWhenUserNotFound() {
            when(userMapper.selectById("ghost")).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> userProfileService.getCurrentUser("ghost"));
            assertEquals("用户不存在", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("updateProfile - 更新个人信息")
    class UpdateProfileTests {

        @Test
        @DisplayName("应成功更新用户名")
        void shouldUpdateUsername() {
            User user = createTestUser();
            when(userMapper.selectById(USER_ID)).thenReturn(user);
            when(userMapper.selectByUsername("newName")).thenReturn(null);

            UpdateProfileRequest request = new UpdateProfileRequest();
            request.setUsername("newName");
            request.setEmail("test@example.com");

            UserVO result = userProfileService.updateProfile(USER_ID, request);

            assertEquals("newName", result.getUsername());
            verify(userMapper).updateById(any(User.class));
        }

        @Test
        @DisplayName("用户名已被其他用户使用时应抛出异常")
        void shouldThrowWhenUsernameTaken() {
            User currentUser = createTestUser();
            when(userMapper.selectById(USER_ID)).thenReturn(currentUser);

            User otherUser = new User();
            otherUser.setUserId("otherUser");
            when(userMapper.selectByUsername("takenName")).thenReturn(otherUser);

            UpdateProfileRequest request = new UpdateProfileRequest();
            request.setUsername("takenName");
            request.setEmail("test@example.com");

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> userProfileService.updateProfile(USER_ID, request));
            assertEquals("用户名已被使用", ex.getMessage());
        }

        @Test
        @DisplayName("邮箱已被其他用户使用时应抛出异常")
        void shouldThrowWhenEmailTaken() {
            User currentUser = createTestUser();
            when(userMapper.selectById(USER_ID)).thenReturn(currentUser);

            User otherUser = new User();
            otherUser.setUserId("otherUser");
            when(userMapper.selectByEmail("taken@test.com")).thenReturn(otherUser);

            UpdateProfileRequest request = new UpdateProfileRequest();
            request.setUsername("testUser");
            request.setEmail("taken@test.com");

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> userProfileService.updateProfile(USER_ID, request));
            assertEquals("邮箱已被使用", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("updatePassword - 修改密码")
    class UpdatePasswordTests {

        @Test
        @DisplayName("当前密码正确且验证码有效时应成功修改")
        void shouldUpdatePasswordWhenCurrentPasswordMatches() {
            User user = createTestUser();
            when(userMapper.selectById(USER_ID)).thenReturn(user);
            when(passwordEncoder.matches("oldPass", "encoded_pass")).thenReturn(true);
            when(passwordEncoder.encode("newPass")).thenReturn("encoded_newPass");

            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("verify:code:test@example.com")).thenReturn("123456");

            UpdatePasswordRequest request = new UpdatePasswordRequest();
            request.setCurrentPassword("oldPass");
            request.setNewPassword("newPass");
            request.setEmail("test@example.com");
            request.setVerificationCode("123456");

            assertDoesNotThrow(() -> userProfileService.updatePassword(USER_ID, request));
            verify(userMapper).updateById(any(User.class));
        }

        @Test
        @DisplayName("当前密码错误时应抛出异常")
        void shouldThrowWhenCurrentPasswordWrong() {
            User user = createTestUser();
            when(userMapper.selectById(USER_ID)).thenReturn(user);
            when(passwordEncoder.matches("wrongPass", "encoded_pass")).thenReturn(false);

            UpdatePasswordRequest request = new UpdatePasswordRequest();
            request.setCurrentPassword("wrongPass");
            request.setNewPassword("newPass");

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> userProfileService.updatePassword(USER_ID, request));
            assertEquals("当前密码错误", ex.getMessage());
        }

        @Test
        @DisplayName("验证码错误时应抛出异常")
        void shouldThrowWhenVerificationCodeWrong() {
            User user = createTestUser();
            when(userMapper.selectById(USER_ID)).thenReturn(user);
            when(passwordEncoder.matches("oldPass", "encoded_pass")).thenReturn(true);

            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("verify:code:test@example.com")).thenReturn("654321");

            UpdatePasswordRequest request = new UpdatePasswordRequest();
            request.setCurrentPassword("oldPass");
            request.setNewPassword("newPass");
            request.setEmail("test@example.com");
            request.setVerificationCode("000000"); // wrong

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> userProfileService.updatePassword(USER_ID, request));
            assertEquals("验证码错误", ex.getMessage());
        }

        @Test
        @DisplayName("验证码过期时应抛出异常")
        void shouldThrowWhenVerificationCodeExpired() {
            User user = createTestUser();
            when(userMapper.selectById(USER_ID)).thenReturn(user);
            when(passwordEncoder.matches("oldPass", "encoded_pass")).thenReturn(true);

            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("verify:code:test@example.com")).thenReturn(null);

            UpdatePasswordRequest request = new UpdatePasswordRequest();
            request.setCurrentPassword("oldPass");
            request.setNewPassword("newPass");
            request.setEmail("test@example.com");
            request.setVerificationCode("123456");

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> userProfileService.updatePassword(USER_ID, request));
            assertEquals("验证码已过期，请重新获取", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("bindEmail - 绑定邮箱")
    class BindEmailTests {

        @Test
        @DisplayName("新邮箱且验证码有效时应成功绑定")
        void shouldBindEmailSuccessfully() {
            User user = createTestUser();
            user.setEmail(null); // 未绑定邮箱
            when(userMapper.selectById(USER_ID)).thenReturn(user);
            when(userMapper.selectByEmail("new@example.com")).thenReturn(null);

            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("bind:email:code:new@example.com")).thenReturn("654321");

            BindEmailRequest request = new BindEmailRequest();
            request.setEmail("new@example.com");
            request.setCode("654321");

            UserVO result = userProfileService.bindEmail(USER_ID, request);

            assertEquals("new@example.com", result.getEmail());
            verify(userMapper).updateById(any(User.class));
        }

        @Test
        @DisplayName("用户已有邮箱时应抛出异常")
        void shouldThrowWhenAlreadyHasEmail() {
            User user = createTestUser();
            when(userMapper.selectById(USER_ID)).thenReturn(user);

            BindEmailRequest request = new BindEmailRequest();
            request.setEmail("another@example.com");
            request.setCode("123456");

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> userProfileService.bindEmail(USER_ID, request));
            assertEquals("您已绑定邮箱，无需重复绑定", ex.getMessage());
        }

        @Test
        @DisplayName("邮箱已被其他用户使用时抛出异常")
        void shouldThrowWhenEmailUsedByOthers() {
            User user = createTestUser();
            user.setEmail(null);
            when(userMapper.selectById(USER_ID)).thenReturn(user);

            User otherUser = new User();
            otherUser.setUserId("other");
            when(userMapper.selectByEmail("taken@example.com")).thenReturn(otherUser);

            BindEmailRequest request = new BindEmailRequest();
            request.setEmail("taken@example.com");
            request.setCode("123456");

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> userProfileService.bindEmail(USER_ID, request));
            assertEquals("该邮箱已被其他用户使用", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("deleteAccount - 注销账户")
    class DeleteAccountTests {

        @Test
        @DisplayName("应删除用户及其所有关联数据")
        void shouldDeleteUserAndRelatedData() {
            when(userMapper.deleteById(USER_ID)).thenReturn(1);

            assertDoesNotThrow(() -> userProfileService.deleteAccount(USER_ID));
            verify(userMapper).deleteById(USER_ID);
        }
    }
}
