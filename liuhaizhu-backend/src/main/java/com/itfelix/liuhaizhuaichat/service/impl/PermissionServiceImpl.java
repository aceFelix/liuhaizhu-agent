package com.itfelix.liuhaizhuaichat.service.impl;

import com.itfelix.liuhaizhuaichat.enums.PermissionType;
import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;
import com.itfelix.liuhaizhuaichat.service.PermissionService;
import org.springframework.stereotype.Service;

/**
 * 权限服务实现类 - 提供权限判断逻辑
 * @author aceFelix
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Override
    public boolean hasPermission(UserRoleEnum userRole, PermissionType permissionType) {
        if (userRole == null) {
            return false;
        }

        // 管理员拥有所有权限
        if (userRole == UserRoleEnum.ADMIN) {
            return true;
        }

        // VIP用户拥有所有权限
        if (userRole == UserRoleEnum.VIP) {
            return true;
        }

        // 普通用户权限判断
        return switch (permissionType) {
            // 普通用户不能上传知识库
            case KNOWLEDGE_BASE_UPLOAD -> false;
            // 普通用户不能使用邮箱工具
            case EMAIL_TOOL -> false;
            // 普通用户不能使用外部MCP服务
            case EXTERNAL_MCP -> false;
            case ALL -> false;
            default -> false;
        };
    }

    @Override
    public boolean hasAnyRole(UserRoleEnum userRole, UserRoleEnum[] allowedRoles) {
        if (userRole == null || allowedRoles == null || allowedRoles.length == 0) {
            return false;
        }

        for (UserRoleEnum role : allowedRoles) {
            if (userRole == role) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isExcluded(UserRoleEnum userRole, UserRoleEnum[] excludedRoles) {
        if (userRole == null || excludedRoles == null || excludedRoles.length == 0) {
            return false;
        }

        for (UserRoleEnum role : excludedRoles) {
            if (userRole == role) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRoleName(UserRoleEnum role) {
        if (role == null) {
            return "未知";
        }
        return switch (role) {
            case ADMIN -> "管理员";
            case VIP -> "VIP用户";
            case USER -> "普通用户";
        };
    }
}
