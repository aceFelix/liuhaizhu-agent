package com.itfelix.liuhaizhuaichat.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 绑定邮箱请求
 * @author aceFelix
 */
@Data
public class BindEmailRequest {
    // 邮箱
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    // 验证码
    @NotBlank(message = "验证码不能为空")
    private String code;
}
