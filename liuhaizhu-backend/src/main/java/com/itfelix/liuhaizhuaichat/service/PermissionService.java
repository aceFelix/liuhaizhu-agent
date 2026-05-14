package com.itfelix.liuhaizhuaichat.service;

import com.itfelix.liuhaizhuaichat.enums.PermissionType;
import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;

/**
 * 权限服务接口 - 提供权限判断逻辑
 * @author aceFelix
 */
public interface PermissionService {

    /**
     * 检查用户是否有指定权限
     * @param userRole 用户角色
     * @param permissionType 权限类型
     * @return 是否有权限
     */
    boolean hasPermission(UserRoleEnum userRole, PermissionType permissionType);

    /**
     * 检查用户是否有任何指定角色
     * @param userRole 用户角色
     * @param allowedRoles 允许的角色数组
     * @return 是否有权限
     */
    boolean hasAnyRole(UserRoleEnum userRole, UserRoleEnum[] allowedRoles);

    /**
     * 检查用户是否被排除
     * @param userRole 用户角色
     * @param excludedRoles 排除的角色数组
     * @return 是否被排除
     */
    boolean isExcluded(UserRoleEnum userRole, UserRoleEnum[] excludedRoles);

    /**
     * 获取用户角色名称
     * @param role 角色枚举
     * @return 角色名称
     */
    String getRoleName(UserRoleEnum role);
}
