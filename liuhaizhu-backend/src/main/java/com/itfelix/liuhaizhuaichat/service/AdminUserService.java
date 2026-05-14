package com.itfelix.liuhaizhuaichat.service;

import com.itfelix.liuhaizhuaichat.pojo.dto.RegisterRequest;
import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import com.itfelix.liuhaizhuaichat.pojo.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口（管理员使用）
 * 用于管理员对用户进行增删改查操作
 * @author aceFelix
 */
public interface AdminUserService {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户实体
     */
    User findByEmail(String email);

    /**
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return 用户实体
     */
    User findById(String userId);

    /**
     * 注册用户
     * @param request 注册请求
     * @return 用户实体
     */
    User register(RegisterRequest request);

    /**
     * 将用户实体转换为VO
     * @param user 用户实体
     * @return 用户VO
     */
    UserVO convertToVO(User user);

    // ==================== 管理员操作 ====================

    /**
     * 获取所有用户列表（管理员）
     * @return 用户VO列表
     */
    List<UserVO> getAllUsers();

    /**
     * 根据ID获取用户信息（管理员）
     * @param userId 用户ID
     * @return 用户VO
     */
    UserVO getUserById(String userId);

    /**
     * 删除用户及其相关数据（管理员）
     * @param userId 用户ID
     */
    void deleteUser(String userId);

    /**
     * 更新用户状态（管理员）
     * @param userId 用户ID
     * @param status 状态（0-禁用，1-启用）
     */
    void updateUserStatus(String userId, Integer status);

    /**
     * 更新用户角色（管理员）
     * @param userId 用户ID
     * @param role 角色（USER, VIP, ADMIN）
     */
    void updateUserRole(String userId, String role);

    /**
     * 管理员创建用户（无需密码和邮箱验证码）
     * @param username 用户名
     * @param email 邮箱
     * @param role 角色
     * @return 创建的用户
     */
    User createUserByAdmin(String username, String email, String role);
}
