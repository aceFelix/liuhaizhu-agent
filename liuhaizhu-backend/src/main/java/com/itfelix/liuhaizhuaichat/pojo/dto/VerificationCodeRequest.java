package com.itfelix.liuhaizhuaichat.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 验证码请求
 * @author aceFelix
 */
@Data
public class VerificationCodeRequest {
    
    /**
     * 邮箱地址
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 验证码类型：password_reset, email_change等
     */
    private String type;
}
