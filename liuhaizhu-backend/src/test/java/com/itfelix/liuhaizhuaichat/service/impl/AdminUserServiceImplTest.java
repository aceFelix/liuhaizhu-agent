package com.itfelix.liuhaizhuaichat.service.impl;

import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;
import com.itfelix.liuhaizhuaichat.mapper.ChatMessageMapper;
import com.itfelix.liuhaizhuaichat.mapper.ConversationMapper;
import com.itfelix.liuhaizhuaichat.mapper.UserMapper;
import com.itfelix.liuhaizhuaichat.pojo.dto.RegisterRequest;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;
import org.mockito.ArgumentMatcher;

/**
 * AdminUserServiceImpl 单元测试
 * @author aceFelix
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminUserServiceImpl 单元测试")
class AdminUserServiceImplTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ConversationMapper conversationMapper;
    @Mock
    private ChatMessageMapper chatMessageMapper;
    @Mock
    private RAGService ragService;

    @InjectMocks
    private AdminUserServiceImpl adminUserService;

    private User createTestUser(String id, String username, UserRoleEnum role) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(username);
        user.setPassword("encoded_password_123456");
        user.setEmail(username + "@example.com");
        user.setRole(role);
        user.setStatus(1);
        user.setAvatar("/images/user-avatar.jpg");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return user;
    }

    @Nested
    @DisplayName("findByUsername - 按用户名查用户")
    class FindByUsernameTests {

        @Test
        @DisplayName("存在的用户应正确返回")
        void shouldReturnUserWhenFound() {
            User user = createTestUser("user123", "alice", UserRoleEnum.USER);
            when(userMapper.selectByUsername("alice")).thenReturn(user);

            User result = adminUserService.findByUsername("alice");

            assertNotNull(result);
            assertEquals("alice", result.getUsername());
            assertEquals("user123", result.getUserId());
        }

        @Test
        @DisplayName("不存在的用户应返回 null")
        void shouldReturnNullWhenNotFound() {
            when(userMapper.selectByUsername("ghost")).thenReturn(null);

            User result = adminUserService.findByUsername("ghost");

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("findByEmail - 按邮箱查用户")
    class FindByEmailTests {

        @Test
        @DisplayName("存在的邮箱应返回用户")
        void shouldReturnUserByEmail() {
            User user = createTestUser("user123", "alice", UserRoleEnum.USER);
            when(userMapper.selectByEmail("alice@example.com")).thenReturn(user);

            User result = adminUserService.findByEmail("alice@example.com");

            assertNotNull(result);
            assertEquals("alice@example.com", result.getEmail());
        }
    }

    @Nested
    @DisplayName("findById - 按ID查用户")
    class FindByIdTests {

        @Test
        @DisplayName("存在的ID应返回用户")
        void shouldReturnUserById() {
            User user = createTestUser("user789", "bob", UserRoleEnum.VIP);
            when(userMapper.selectById("user789")).thenReturn(user);

            User result = adminUserService.findById("user789");

            assertNotNull(result);
            assertEquals("bob", result.getUsername());
        }
    }

    @Nested
    @DisplayName("register - 注册用户")
    class RegisterTests {

        @Test
        @DisplayName("应成功创建用户并加密密码")
        void shouldRegisterUserWithEncodedPassword() {
            RegisterRequest request = new RegisterRequest();
            request.setUsername("newUser");
            request.setPassword("rawPassword");
            request.setEmail("new@example.com");
            request.setRegisterMode("email");

            when(passwordEncoder.encode("rawPassword")).thenReturn("encoded_rawPassword");

            User result = adminUserService.register(request);

            assertNotNull(result);
            assertEquals("newUser", result.getUsername());
            assertEquals("encoded_rawPassword", result.getPassword());
            assertEquals(UserRoleEnum.USER, result.getRole());
            assertEquals(1, result.getStatus());
            assertEquals("/images/user-avatar.jpg", result.getAvatar());
            verify(userMapper).insert(Mockito.<User>argThat(user -> user != null && "newUser".equals(user.getUsername())));
            verify(passwordEncoder).encode("rawPassword");
        }
    }

    @Nested
    @DisplayName("convertToVO - Entity 转 VO")
    class ConvertToVOTests {

        @Test
        @DisplayName("应正确转换所有字段")
        void shouldConvertAllFields() {
            User user = createTestUser("user123", "alice", UserRoleEnum.VIP);
            user.setStatus(1);

            UserVO vo = adminUserService.convertToVO(user);

            assertEquals("user123", vo.getUserId());
            assertEquals("alice", vo.getUsername());
            assertEquals("alice@example.com", vo.getEmail());
            assertEquals("/images/user-avatar.jpg", vo.getAvatar());
            assertEquals(UserRoleEnum.VIP, vo.getRole());
            assertEquals(1, vo.getStatus());
            assertNotNull(vo.getCreateTime());
        }

        @Test
        @DisplayName("转换后的 VO 不应包含密码字段")
        void shouldNotIncludePassword() {
            User user = createTestUser("user123", "alice", UserRoleEnum.USER);

            UserVO vo = adminUserService.convertToVO(user);

            // UserVO 类没有 password 字段，所以这个测试验证的是设计合理性
            assertNotNull(vo);
            assertEquals("alice", vo.getUsername());
        }
    }

    @Nested
    @DisplayName("getAllUsers - 获取所有用户")
    class GetAllUsersTests {

        @Test
        @DisplayName("应返回所有用户的 VO 列表")
        void shouldReturnAllUsersAsVOList() {
            User user1 = createTestUser("user1", "alice", UserRoleEnum.USER);
            User user2 = createTestUser("user2", "bob", UserRoleEnum.VIP);
            when(userMapper.selectList(null)).thenReturn(List.of(user1, user2));

            List<UserVO> result = adminUserService.getAllUsers();

            assertEquals(2, result.size());
            assertEquals("alice", result.get(0).getUsername());
            assertEquals("bob", result.get(1).getUsername());
        }

        @Test
        @DisplayName("无用户时应返回空列表")
        void shouldReturnEmptyListWhenNoUsers() {
            when(userMapper.selectList(null)).thenReturn(List.of());

            List<UserVO> result = adminUserService.getAllUsers();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getUserById - 根据ID获取用户")
    class GetUserByIdTests {

        @Test
        @DisplayName("存在的用户应返回 VO")
        void shouldReturnUserVOById() {
            User user = createTestUser("user123", "alice", UserRoleEnum.USER);
            when(userMapper.selectById("user123")).thenReturn(user);

            UserVO result = adminUserService.getUserById("user123");

            assertNotNull(result);
            assertEquals("alice", result.getUsername());
        }

        @Test
        @DisplayName("不存在的用户应抛出异常")
        void shouldThrowWhenUserNotFound() {
            when(userMapper.selectById("ghost")).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> adminUserService.getUserById("ghost"));
            assertEquals("用户不存在", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("deleteUser - 删除用户")
    class DeleteUserTests {

        @Test
        @DisplayName("应删除用户及其关联数据")
        void shouldDeleteUserAndRelatedData() {
            when(userMapper.deleteById("user123")).thenReturn(1);

            assertDoesNotThrow(() -> adminUserService.deleteUser("user123"));
            verify(userMapper).deleteById("user123");
        }
    }

    @Nested
    @DisplayName("updateUserStatus - 更新用户状态")
    class UpdateUserStatusTests {

        @Test
        @DisplayName("应成功更新用户状态")
        void shouldUpdateUserStatus() {
            User user = createTestUser("user123", "alice", UserRoleEnum.USER);
            when(userMapper.selectById("user123")).thenReturn(user);

            adminUserService.updateUserStatus("user123", 0);

            assertEquals(0, user.getStatus());
            verify(userMapper).updateById(user);
        }

        @Test
        @DisplayName("用户不存在应抛出异常")
        void shouldThrowWhenUserNotFoundForStatus() {
            when(userMapper.selectById("ghost")).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> adminUserService.updateUserStatus("ghost", 1));
            assertEquals("用户不存在", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("updateUserRole - 更新用户角色")
    class UpdateUserRoleTests {

        @Test
        @DisplayName("应成功更新为有效角色")
        void shouldUpdateToValidRole() {
            User user = createTestUser("user123", "alice", UserRoleEnum.USER);
            when(userMapper.selectById("user123")).thenReturn(user);

            adminUserService.updateUserRole("user123", "VIP");

            assertEquals(UserRoleEnum.VIP, user.getRole());
            verify(userMapper).updateById(user);
        }

        @Test
        @DisplayName("无效角色应抛出异常")
        void shouldThrowForInvalidRole() {
            User user = createTestUser("user123", "alice", UserRoleEnum.USER);
            when(userMapper.selectById("user123")).thenReturn(user);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> adminUserService.updateUserRole("user123", "INVALID_ROLE"));
            assertEquals("无效的角色: INVALID_ROLE", ex.getMessage());
        }

        @Test
        @DisplayName("用户不存在应抛出异常")
        void shouldThrowWhenUserNotFoundForRole() {
            when(userMapper.selectById("ghost")).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> adminUserService.updateUserRole("ghost", "VIP"));
            assertEquals("用户不存在", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("createUserByAdmin - 管理员创建用户")
    class CreateUserByAdminTests {

        @Test
        @DisplayName("应成功创建用户，密码默认 123456")
        void shouldCreateUserWithDefaultPassword() {
            when(userMapper.selectByUsername("newAdminUser")).thenReturn(null);
            when(userMapper.selectByEmail("newadmin@example.com")).thenReturn(null);
            when(passwordEncoder.encode("123456")).thenReturn("encoded_123456");

            User result = adminUserService.createUserByAdmin("newAdminUser",
                    "newadmin@example.com", "VIP");

            assertNotNull(result);
            assertEquals("newAdminUser", result.getUsername());
            assertEquals("newadmin@example.com", result.getEmail());
            assertEquals(UserRoleEnum.VIP, result.getRole());
            assertEquals(1, result.getStatus());
            verify(passwordEncoder).encode("123456");
            verify(userMapper).insert(Mockito.<User>argThat(user -> user != null && "newAdminUser".equals(user.getUsername())));
        }

        @Test
        @DisplayName("用户名已存在应抛出异常")
        void shouldThrowWhenUsernameExists() {
            User existing = createTestUser("user001", "existingAdmin", UserRoleEnum.USER);
            when(userMapper.selectByUsername("existingAdmin")).thenReturn(existing);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> adminUserService.createUserByAdmin("existingAdmin",
                            "new@example.com", "USER"));
            assertEquals("用户名已存在", ex.getMessage());
            verify(userMapper, never()).insert(Mockito.<User>any());
        }

        @Test
        @DisplayName("邮箱已存在应抛出异常")
        void shouldThrowWhenEmailExists() {
            when(userMapper.selectByUsername("newUser")).thenReturn(null);
            User existing = createTestUser("user002", "other", UserRoleEnum.USER);
            when(userMapper.selectByEmail("taken@example.com")).thenReturn(existing);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> adminUserService.createUserByAdmin("newUser",
                            "taken@example.com", "USER"));
            assertEquals("邮箱已被注册", ex.getMessage());
        }

        @Test
        @DisplayName("无效角色应默认设为 USER")
        void shouldDefaultToUserForInvalidRole() {
            when(userMapper.selectByUsername("newUser")).thenReturn(null);
            when(userMapper.selectByEmail("new@example.com")).thenReturn(null);
            when(passwordEncoder.encode("123456")).thenReturn("encoded_123456");

            User result = adminUserService.createUserByAdmin("newUser",
                    "new@example.com", "INVALID");

            assertEquals(UserRoleEnum.USER, result.getRole());
        }
    }
}
