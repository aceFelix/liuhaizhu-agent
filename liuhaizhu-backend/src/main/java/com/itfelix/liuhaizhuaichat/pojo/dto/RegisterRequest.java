package com.itfelix.liuhaizhuaichat.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求
 * @author aceFelix
 */
@Data
public class RegisterRequest {
    // 用户名
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20位之间")
    @Pattern(regexp = "^[\u4e00-\u9fa5a-zA-Z0-9_]+$", message = "用户名只能包含汉字、字母、数字和下划线")
    private String username;
    // 邮箱（邮箱注册时必填，用户名注册时可为空）
    @Email(message = "邮箱格式不正确")
    private String email;
    // 密码
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;
    // 注册方式：email-邮箱注册，username-用户名注册
    @NotBlank(message = "注册方式不能为空")
    private String registerMode;
    // 邮箱验证码（邮箱注册时必填）
    private String code;

}
