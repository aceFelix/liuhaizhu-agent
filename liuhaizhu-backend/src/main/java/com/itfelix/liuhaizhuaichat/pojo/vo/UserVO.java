package com.itfelix.liuhaizhuaichat.pojo.vo;

import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    // 用户ID
    private String userId;
    // 用户名
    private String username;
    // 邮箱
    private String email;
    // 头像
    private String avatar;
    // 角色
    private UserRoleEnum role;
    // 状态
    private Integer status;
    // 创建时间
    private LocalDateTime createTime;
}
