package com.itfelix.liuhaizhuaichat.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author aceFelix
 */
@Data
public class LoginRequest {
    // 用户名
    @NotBlank(message = "用户名不能为空")
    private String username;
    // 密码
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;
}
