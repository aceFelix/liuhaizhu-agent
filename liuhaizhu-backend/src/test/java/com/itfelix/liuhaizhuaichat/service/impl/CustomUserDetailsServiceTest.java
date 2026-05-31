package com.itfelix.liuhaizhuaichat.service.impl;

import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;
import com.itfelix.liuhaizhuaichat.mapper.UserMapper;
import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * CustomUserDetailsService 单元测试
 * @author aceFelix
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService 单元测试")
class CustomUserDetailsServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private com.itfelix.liuhaizhuaichat.security.CustomUserDetailsService userDetailsService;

    @Nested
    @DisplayName("loadUserByUsername - 加载用户")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("用户存在时应返回 UserDetails")
        void shouldReturnUserDetailsWhenUserExists() {
            User user = new User();
            user.setUserId("user123");
            user.setUsername("alice");
            user.setPassword("encoded_pass");
            user.setRole(UserRoleEnum.USER);
            user.setStatus(1);

            when(userMapper.selectOne(any())).thenReturn(user);

            UserDetails details = userDetailsService.loadUserByUsername("alice");

            assertNotNull(details);
            assertEquals("alice", details.getUsername());
            assertEquals("encoded_pass", details.getPassword());
            assertTrue(details.isEnabled());
        }

        @Test
        @DisplayName("用户不存在时应抛出 UsernameNotFoundException")
        void shouldThrowWhenUserNotFound() {
            when(userMapper.selectOne(any())).thenReturn(null);

            assertThrows(UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername("ghost"));
        }

        @Test
        @DisplayName("禁用用户应正确反映 isEnabled")
        void shouldReflectDisabledStatus() {
            User user = new User();
            user.setUserId("user456");
            user.setUsername("disabled_user");
            user.setPassword("pass");
            user.setRole(UserRoleEnum.USER);
            user.setStatus(0); // disabled

            when(userMapper.selectOne(any())).thenReturn(user);

            UserDetails details = userDetailsService.loadUserByUsername("disabled_user");

            assertNotNull(details);
            assertFalse(details.isEnabled());
        }

        @Test
        @DisplayName("管理员用户应有 ROLE_ADMIN 权限")
        void adminShouldHaveAdminAuthority() {
            User user = new User();
            user.setUserId("admin1");
            user.setUsername("admin");
            user.setPassword("pass");
            user.setRole(UserRoleEnum.ADMIN);
            user.setStatus(1);

            when(userMapper.selectOne(any())).thenReturn(user);

            UserDetails details = userDetailsService.loadUserByUsername("admin");

            assertTrue(details.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        }
    }
}
