package com.itfelix.liuhaizhuaichat.service.impl;

import com.itfelix.liuhaizhuaichat.enums.PermissionType;
import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PermissionServiceImpl 单元测试（纯逻辑，无需 Mock）
 * @author aceFelix
 */
@DisplayName("PermissionServiceImpl 单元测试")
class PermissionServiceImplTest {

    private final PermissionServiceImpl permissionService = new PermissionServiceImpl();

    @Nested
    @DisplayName("hasPermission - 权限判断")
    class HasPermissionTests {

        @Test
        @DisplayName("ADMIN 对所有权限返回 true")
        void adminShouldHaveAllPermissions() {
            assertTrue(permissionService.hasPermission(UserRoleEnum.ADMIN, PermissionType.KNOWLEDGE_BASE_UPLOAD));
            assertTrue(permissionService.hasPermission(UserRoleEnum.ADMIN, PermissionType.EMAIL_TOOL));
            assertTrue(permissionService.hasPermission(UserRoleEnum.ADMIN, PermissionType.EXTERNAL_MCP));
            assertTrue(permissionService.hasPermission(UserRoleEnum.ADMIN, PermissionType.ALL));
        }

        @Test
        @DisplayName("VIP 对所有权限返回 true")
        void vipShouldHaveAllPermissions() {
            assertTrue(permissionService.hasPermission(UserRoleEnum.VIP, PermissionType.KNOWLEDGE_BASE_UPLOAD));
            assertTrue(permissionService.hasPermission(UserRoleEnum.VIP, PermissionType.EMAIL_TOOL));
            assertTrue(permissionService.hasPermission(UserRoleEnum.VIP, PermissionType.EXTERNAL_MCP));
        }

        @Test
        @DisplayName("USER 对 KNOWLEDGE_BASE_UPLOAD 返回 false")
        void userShouldNotHaveUploadPermission() {
            assertFalse(permissionService.hasPermission(UserRoleEnum.USER, PermissionType.KNOWLEDGE_BASE_UPLOAD));
        }

        @Test
        @DisplayName("USER 对 EMAIL_TOOL 返回 false")
        void userShouldNotHaveEmailToolPermission() {
            assertFalse(permissionService.hasPermission(UserRoleEnum.USER, PermissionType.EMAIL_TOOL));
        }

        @Test
        @DisplayName("USER 对 EXTERNAL_MCP 返回 false")
        void userShouldNotHaveExternalMcpPermission() {
            assertFalse(permissionService.hasPermission(UserRoleEnum.USER, PermissionType.EXTERNAL_MCP));
        }

        @Test
        @DisplayName("userRole 为 null 时返回 false")
        void shouldReturnFalseForNullRole() {
            assertFalse(permissionService.hasPermission(null, PermissionType.KNOWLEDGE_BASE_UPLOAD));
        }
    }

    @Nested
    @DisplayName("hasAnyRole - 角色包含判断")
    class HasAnyRoleTests {

        @Test
        @DisplayName("用户在允许列表中时应返回 true")
        void shouldReturnTrueWhenInAllowedList() {
            assertTrue(permissionService.hasAnyRole(UserRoleEnum.ADMIN,
                    new UserRoleEnum[]{UserRoleEnum.ADMIN, UserRoleEnum.VIP}));
        }

        @Test
        @DisplayName("用户不在允许列表中时应返回 false")
        void shouldReturnFalseWhenNotInAllowedList() {
            assertFalse(permissionService.hasAnyRole(UserRoleEnum.USER,
                    new UserRoleEnum[]{UserRoleEnum.ADMIN, UserRoleEnum.VIP}));
        }

        @Test
        @DisplayName("userRole 为 null 时应返回 false")
        void shouldReturnFalseForNullRole() {
            assertFalse(permissionService.hasAnyRole(null,
                    new UserRoleEnum[]{UserRoleEnum.ADMIN}));
        }

        @Test
        @DisplayName("allowedRoles 为空时应返回 false")
        void shouldReturnFalseForEmptyAllowed() {
            assertFalse(permissionService.hasAnyRole(UserRoleEnum.ADMIN,
                    new UserRoleEnum[]{}));
        }

        @Test
        @DisplayName("allowedRoles 为 null 时应返回 false")
        void shouldReturnFalseForNullAllowed() {
            assertFalse(permissionService.hasAnyRole(UserRoleEnum.ADMIN, null));
        }
    }

    @Nested
    @DisplayName("isExcluded - 排除角色判断")
    class IsExcludedTests {

        @Test
        @DisplayName("用户在排除列表中时应返回 true")
        void shouldReturnTrueWhenExcluded() {
            assertTrue(permissionService.isExcluded(UserRoleEnum.USER,
                    new UserRoleEnum[]{UserRoleEnum.USER}));
        }

        @Test
        @DisplayName("用户不在排除列表中时应返回 false")
        void shouldReturnFalseWhenNotExcluded() {
            assertFalse(permissionService.isExcluded(UserRoleEnum.ADMIN,
                    new UserRoleEnum[]{UserRoleEnum.USER}));
        }

        @Test
        @DisplayName("userRole 为 null 时应返回 false")
        void shouldReturnFalseForNullRole() {
            assertFalse(permissionService.isExcluded(null,
                    new UserRoleEnum[]{UserRoleEnum.USER}));
        }
    }

    @Nested
    @DisplayName("getRoleName - 角色名称")
    class GetRoleNameTests {

        @Test
        @DisplayName("应返回中文角色名称")
        void shouldReturnChineseRoleName() {
            assertEquals("管理员", permissionService.getRoleName(UserRoleEnum.ADMIN));
            assertEquals("VIP用户", permissionService.getRoleName(UserRoleEnum.VIP));
            assertEquals("普通用户", permissionService.getRoleName(UserRoleEnum.USER));
        }

        @Test
        @DisplayName("role 为 null 时应返回'未知'")
        void shouldReturnUnknownForNull() {
            assertEquals("未知", permissionService.getRoleName(null));
        }
    }
}
