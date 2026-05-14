package com.itfelix.liuhaizhuaichat.controller;

import com.itfelix.liuhaizhuaichat.pojo.dto.BindEmailRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.UpdatePasswordRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.UpdateProfileRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.VerificationCodeRequest;
import com.itfelix.liuhaizhuaichat.pojo.vo.UserVO;
import com.itfelix.liuhaizhuaichat.security.CustomUserDetails;
import com.itfelix.liuhaizhuaichat.service.UserProfileService;
import com.itfelix.liuhaizhuaichat.utils.AceResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 用户个人信息控制器
 * 用于用户自己操作自己的个人信息（查看、修改、删除账户等）
 * @author aceFelix
 */
@RestController
@RequestMapping("/api/user/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * 获取当前登录用户的个人信息
     * @param authentication 认证信息
     * @return 用户信息
     */
    @GetMapping
    public AceResult<UserVO> getCurrentUser(Authentication authentication) {
        String userId = extractUserId(authentication);
        UserVO userVO = userProfileService.getCurrentUser(userId);
        return AceResult.success(userVO);
    }

    /**
     * 更新当前登录用户的个人信息
     * @param request 更新请求
     * @param authentication 认证信息
     * @return 更新后的用户信息
     */
    @PutMapping
    public AceResult<UserVO> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        String userId = extractUserId(authentication);
        UserVO userVO = userProfileService.updateProfile(userId, request);
        return AceResult.success(userVO);
    }

    /**
     * 修改当前登录用户的密码
     * @param request 密码修改请求
     * @param authentication 认证信息
     * @return 修改结果
     */
    @PutMapping("/password")
    public AceResult<Void> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request,
            Authentication authentication) {
        String userId = extractUserId(authentication);
        userProfileService.updatePassword(userId, request);
        return AceResult.success();
    }

    /**
     * 发送邮箱验证码（用于修改密码或邮箱）
     * @param request 验证码请求
     * @param authentication 认证信息
     * @return 发送结果
     */
    @PostMapping("/send-verification-code")
    public AceResult<Void> sendVerificationCode(
            @Valid @RequestBody VerificationCodeRequest request,
            Authentication authentication) {
        String userId = extractUserId(authentication);
        userProfileService.sendVerificationCode(userId, request);
        return AceResult.success();
    }

    /**
     * 发送邮箱绑定验证码
     * @param request 验证码请求
     * @param authentication 认证信息
     * @return 发送结果
     */
    @PostMapping("/send-bind-email-code")
    public AceResult<Void> sendBindEmailCode(
            @Valid @RequestBody VerificationCodeRequest request,
            Authentication authentication) {
        String userId = extractUserId(authentication);
        userProfileService.sendBindEmailCode(userId, request);
        return AceResult.success();
    }

    /**
     * 绑定邮箱
     * @param request 绑定邮箱请求
     * @param authentication 认证信息
     * @return 绑定结果
     */
    @PostMapping("/bind-email")
    public AceResult<UserVO> bindEmail(
            @Valid @RequestBody BindEmailRequest request,
            Authentication authentication) {
        String userId = extractUserId(authentication);
        UserVO userVO = userProfileService.bindEmail(userId, request);
        return AceResult.success(userVO);
    }

    /**
     * 注销当前登录用户的账户
     * @param authentication 认证信息
     * @return 注销结果
     */
    @DeleteMapping
    public AceResult<Void> deleteAccount(Authentication authentication) {
        String userId = extractUserId(authentication);
        userProfileService.deleteAccount(userId);
        return AceResult.success();
    }

    /**
     * 从Authentication中提取userId
     * @param authentication 认证信息
     * @return userId
     */
    private String extractUserId(Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("用户未登录");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        }
        throw new RuntimeException("无法获取用户信息");
    }
}
