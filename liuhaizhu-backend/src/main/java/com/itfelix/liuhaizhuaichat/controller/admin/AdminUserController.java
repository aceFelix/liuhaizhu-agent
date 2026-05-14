package com.itfelix.liuhaizhuaichat.controller.admin;

import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import com.itfelix.liuhaizhuaichat.pojo.vo.UserVO;
import com.itfelix.liuhaizhuaichat.service.AdminUserService;
import com.itfelix.liuhaizhuaichat.utils.AceResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器（管理员使用）
 * 用于管理员对用户进行增删改查操作
 * @author aceFelix
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * 获取所有用户列表（管理员）
     * @return 用户列表
     */
    @GetMapping
    public AceResult<List<UserVO>> getAllUsers() {
        List<UserVO> users = adminUserService.getAllUsers();
        return AceResult.success(users);
    }

    /**
     * 根据ID获取用户信息（管理员）
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    public AceResult<UserVO> getUserById(@PathVariable String userId) {
        UserVO userVO = adminUserService.getUserById(userId);
        return AceResult.success(userVO);
    }

    /**
     * 删除用户（管理员）
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{userId}")
    public AceResult<Void> deleteUser(@PathVariable String userId) {
        adminUserService.deleteUser(userId);
        return AceResult.success();
    }

    /**
     * 更新用户状态（管理员）
     * @param userId 用户ID
     * @param status 状态（0-禁用，1-启用）
     * @return 更新结果
     */
    @PutMapping("/{userId}/status")
    public AceResult<Void> updateUserStatus(
            @PathVariable String userId,
            @RequestParam Integer status) {
        adminUserService.updateUserStatus(userId, status);
        return AceResult.success();
    }

    /**
     * 更新用户角色（管理员）
     * @param userId 用户ID
     * @param role 角色（USER, VIP, ADMIN）
     * @return 更新结果
     */
    @PutMapping("/{userId}/role")
    public AceResult<Void> updateUserRole(
            @PathVariable String userId,
            @RequestParam String role) {
        adminUserService.updateUserRole(userId, role);
        return AceResult.success();
    }

    /**
     * 管理员创建用户（无需密码和邮箱验证码，默认密码123456）
     * @param request 包含username、email、role的请求体
     * @return 创建的用户信息
     */
    @PostMapping
    public AceResult<UserVO> createUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String role = request.get("role");

        if (username == null || username.trim().isEmpty()) {
            return AceResult.error("用户名不能为空");
        }
        if (email == null || email.trim().isEmpty()) {
            return AceResult.error("邮箱不能为空");
        }

        User user = adminUserService.createUserByAdmin(username.trim(), email.trim(), role);
        return AceResult.success(adminUserService.convertToVO(user));
    }
}
