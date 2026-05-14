package com.itfelix.liuhaizhuaichat.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    // 访问令牌
    private String token;
    // 刷新令牌
    private String refreshToken;
    // 用户信息
    private UserVO user;
}
