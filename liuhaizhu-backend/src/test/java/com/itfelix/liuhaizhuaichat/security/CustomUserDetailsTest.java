package com.itfelix.liuhaizhuaichat.security;

import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;
import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CustomUserDetails 单元测试
 * @author aceFelix
 */
@DisplayName("CustomUserDetails 单元测试")
class CustomUserDetailsTest {

    private User createTestUser(String id, String username, String password,
                                UserRoleEnum role, Integer status) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        user.setStatus(status);
        return user;
    }

    @Nested
    @DisplayName("构造与基础方法")
    class BasicTests {

        @Test
        @DisplayName("getUsername 应返回用户名")
        void shouldReturnUsername() {
            User user = createTestUser("u1", "alice", "pass", UserRoleEnum.USER, 1);
            CustomUserDetails details = new CustomUserDetails(user);
            assertEquals("alice", details.getUsername());
        }

        @Test
        @DisplayName("getPassword 应返回密码")
        void shouldReturnPassword() {
            User user = createTestUser("u1", "alice", "encoded123", UserRoleEnum.USER, 1);
            CustomUserDetails details = new CustomUserDetails(user);
            assertEquals("encoded123", details.getPassword());
        }

        @Test
        @DisplayName("getUserId 应返回用户ID")
        void shouldReturnUserId() {
            User user = createTestUser("user_abc", "alice", "pass", UserRoleEnum.USER, 1);
            CustomUserDetails details = new CustomUserDetails(user);
            assertEquals("user_abc", details.getUserId());
        }

        @Test
        @DisplayName("getRole 应返回角色枚举")
        void shouldReturnRole() {
            User user = createTestUser("u1", "admin", "pass", UserRoleEnum.ADMIN, 1);
            CustomUserDetails details = new CustomUserDetails(user);
            assertEquals(UserRoleEnum.ADMIN, details.getRole());
        }
    }

    @Nested
    @DisplayName("getAuthorities - 权限")
    class AuthoritiesTests {

        @Test
        @DisplayName("ADMIN 应有 ROLE_ADMIN 权限")
        void adminShouldHaveAdminAuthority() {
            User user = createTestUser("u1", "admin", "pass", UserRoleEnum.ADMIN, 1);
            CustomUserDetails details = new CustomUserDetails(user);

            Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
            assertEquals(1, authorities.size());
            assertTrue(authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        }

        @Test
        @DisplayName("USER 应有 ROLE_USER 权限")
        void userShouldHaveUserAuthority() {
            User user = createTestUser("u1", "user", "pass", UserRoleEnum.USER, 1);
            CustomUserDetails details = new CustomUserDetails(user);

            Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
            assertTrue(authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        }

        @Test
        @DisplayName("VIP 应有 ROLE_VIP 权限")
        void vipShouldHaveVipAuthority() {
            User user = createTestUser("u1", "vip", "pass", UserRoleEnum.VIP, 1);
            CustomUserDetails details = new CustomUserDetails(user);

            Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
            assertTrue(authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_VIP")));
        }
    }

    @Nested
    @DisplayName("账户状态方法")
    class AccountStatusTests {

        @Test
        @DisplayName("isAccountNonExpired 应始终为 true")
        void accountShouldNeverExpire() {
            User user = createTestUser("u1", "test", "pass", UserRoleEnum.USER, 1);
            CustomUserDetails details = new CustomUserDetails(user);
            assertTrue(details.isAccountNonExpired());
        }

        @Test
        @DisplayName("isCredentialsNonExpired 应始终为 true")
        void credentialsShouldNeverExpire() {
            User user = createTestUser("u1", "test", "pass", UserRoleEnum.USER, 1);
            CustomUserDetails details = new CustomUserDetails(user);
            assertTrue(details.isCredentialsNonExpired());
        }

        @Test
        @DisplayName("status=1 时 isEnabled 应为 true")
        void shouldBeEnabledWhenStatusIsOne() {
            User user = createTestUser("u1", "active", "pass", UserRoleEnum.USER, 1);
            CustomUserDetails details = new CustomUserDetails(user);
            assertTrue(details.isEnabled());
        }

        @Test
        @DisplayName("status=0 时 isEnabled 应为 false")
        void shouldBeDisabledWhenStatusIsZero() {
            User user = createTestUser("u1", "disabled", "pass", UserRoleEnum.USER, 0);
            CustomUserDetails details = new CustomUserDetails(user);
            assertFalse(details.isEnabled());
        }

        @Test
        @DisplayName("status=2 时 isAccountNonLocked 应为 false")
        void shouldBeLockedWhenStatusIsTwo() {
            User user = createTestUser("u1", "locked", "pass", UserRoleEnum.USER, 2);
            CustomUserDetails details = new CustomUserDetails(user);
            assertFalse(details.isAccountNonLocked());
        }

        @Test
        @DisplayName("status=1 时 isAccountNonLocked 应为 true")
        void shouldBeUnlockedWhenStatusIsOne() {
            User user = createTestUser("u1", "normal", "pass", UserRoleEnum.USER, 1);
            CustomUserDetails details = new CustomUserDetails(user);
            assertTrue(details.isAccountNonLocked());
        }
    }
}
