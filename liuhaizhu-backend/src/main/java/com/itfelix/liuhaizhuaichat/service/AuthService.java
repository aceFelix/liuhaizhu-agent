package com.itfelix.liuhaizhuaichat.service;

import com.itfelix.liuhaizhuaichat.pojo.dto.LoginRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.RegisterRequest;
import com.itfelix.liuhaizhuaichat.pojo.vo.LoginResponse;
import com.itfelix.liuhaizhuaichat.pojo.vo.UserVO;

/**
 * 认证服务接口
 * @author aceFelix
 */
public interface AuthService {

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应（包含token和用户信息）
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户注册
     * @param request 注册请求
     * @return 登录响应（包含token和用户信息）
     */
    LoginResponse register(RegisterRequest request);

    /**
     * 刷新token
     * @param refreshToken 刷新token
     * @return 登录响应（包含新的token和用户信息）
     */
    LoginResponse refreshToken(String refreshToken);

    /**
     * 获取当前用户信息（通过用户名）
     * @param username 用户名
     * @return 用户信息
     */
    UserVO getCurrentUser(String username);

    /**
     * 获取当前用户信息（通过用户ID）
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getCurrentUserById(String userId);

    /**
     * 删除账户（注销用户）
     * @param username 用户名
     */
    void deleteAccount(String username);

    /**
     * 删除账户（注销用户）- 通过用户ID
     * @param userId 用户ID
     */
    void deleteAccountById(String userId);

    /**
     * 发送注册验证码
     * @param email 邮箱
     */
    void sendRegisterVerificationCode(String email);

    /**
     * 升级用户为VIP
     * @param userId 用户ID
     */
    void upgradeToVip(String userId);
}
