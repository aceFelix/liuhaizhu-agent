package com.itfelix.liuhaizhuaichat.controller;

import com.itfelix.liuhaizhuaichat.pojo.dto.LoginRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.RegisterRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.SendRegisterCodeRequest;
import com.itfelix.liuhaizhuaichat.pojo.vo.LoginResponse;
import com.itfelix.liuhaizhuaichat.pojo.vo.RefreshTokenRequest;
import com.itfelix.liuhaizhuaichat.pojo.vo.UserVO;
import com.itfelix.liuhaizhuaichat.security.CustomUserDetails;
import com.itfelix.liuhaizhuaichat.service.AuthService;
import com.itfelix.liuhaizhuaichat.utils.AceResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 登录认证控制器
 * Controller层：只负责接收请求和返回响应，业务逻辑委托给Service层
 * @author aceFelix
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 登录
     * @param request 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public AceResult<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return AceResult.success(response);
        } catch (RuntimeException e) {
            return AceResult.error(e.getMessage());
        }
    }

    /**
     * 注册
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public AceResult<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse response = authService.register(request);
            return AceResult.success(response);
        } catch (RuntimeException e) {
            return AceResult.error(e.getMessage());
        }
    }

    /**
     * 发送注册验证码
     * @param request 发送验证码请求
     * @return 发送结果
     */
    @PostMapping("/send-register-code")
    public AceResult<Void> sendRegisterCode(@Valid @RequestBody SendRegisterCodeRequest request) {
        try {
            authService.sendRegisterVerificationCode(request.getEmail());
            return AceResult.success();
        } catch (RuntimeException e) {
            return AceResult.error(e.getMessage());
        }
    }

    /**
     * 刷新token
     * @param request 刷新token请求
     * @return 刷新token结果
     */
    @PostMapping("/refresh")
    public AceResult<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            LoginResponse response = authService.refreshToken(request.getRefreshToken());
            return AceResult.success(response);
        } catch (RuntimeException e) {
            return AceResult.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     * @param authentication 认证信息
     * @return 当前用户信息
     */
    @GetMapping("/me")
    public AceResult<UserVO> getCurrentUser(Authentication authentication) {
        try {
            // 从Principal中获取userId
            String userId = extractUserId(authentication);
            UserVO userVO = authService.getCurrentUserById(userId);
            return AceResult.success(userVO);
        } catch (RuntimeException e) {
            return AceResult.error(e.getMessage());
        }
    }

    /**
     * 注销账户（删除用户及其所有数据）
     * @param authentication 认证信息
     * @return 注销结果
     */
    @DeleteMapping("/delete-account")
    public AceResult<Void> deleteAccount(Authentication authentication) {
        try {
            String userId = extractUserId(authentication);
            authService.deleteAccountById(userId);
            return AceResult.success();
        } catch (RuntimeException e) {
            return AceResult.error(e.getMessage());
        }
    }

    /**
     * 升级为VIP用户
     * @param authentication 认证信息
     * @return 升级结果
     */
    @PostMapping("/upgrade-vip")
    public AceResult<Void> upgradeToVip(Authentication authentication) {
        try {
            String userId = extractUserId(authentication);
            authService.upgradeToVip(userId);
            return AceResult.success();
        } catch (RuntimeException e) {
            return AceResult.error(e.getMessage());
        }
    }

    /**
     * 从Authentication中提取userId
     * @param authentication 认证信息
     * @return userId
     */
    private String extractUserId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        }
        return null;
    }
}
