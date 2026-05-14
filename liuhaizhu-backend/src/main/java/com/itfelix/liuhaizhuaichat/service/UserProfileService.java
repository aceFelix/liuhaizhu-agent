package com.itfelix.liuhaizhuaichat.service;

import com.itfelix.liuhaizhuaichat.pojo.dto.BindEmailRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.UpdatePasswordRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.UpdateProfileRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.VerificationCodeRequest;
import com.itfelix.liuhaizhuaichat.pojo.vo.UserVO;

/**
 * 用户个人信息服务接口
 * 用于用户自己操作自己的个人信息
 * @author aceFelix
 */
public interface UserProfileService {

    /**
     * 获取当前用户信息
     * @param userId 用户ID
     * @return 用户VO
     */
    UserVO getCurrentUser(String userId);

    /**
     * 更新用户个人信息
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新后的用户VO
     */
    UserVO updateProfile(String userId, UpdateProfileRequest request);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param request 密码修改请求
     */
    void updatePassword(String userId, UpdatePasswordRequest request);

    /**
     * 发送邮箱验证码
     * @param userId 用户ID
     * @param request 验证码请求
     */
    void sendVerificationCode(String userId, VerificationCodeRequest request);

    /**
     * 发送邮箱绑定验证码
     * @param userId 用户ID
     * @param request 验证码请求
     */
    void sendBindEmailCode(String userId, VerificationCodeRequest request);

    /**
     * 绑定邮箱
     * @param userId 用户ID
     * @param request 绑定邮箱请求
     * @return 更新后的用户VO
     */
    UserVO bindEmail(String userId, BindEmailRequest request);

    /**
     * 注销账户（删除用户及其所有数据）
     * @param userId 用户ID
     */
    void deleteAccount(String userId);
}
