package com.itfelix.liuhaizhuaichat.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itfelix.liuhaizhuaichat.handler.UserRoleEnumTypeHandler;
import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
/**
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("user")
public class User {
    // 用户ID
    @TableId(type = IdType.ASSIGN_ID)
    private String userId;
    // 用户名
    private String username;
    // 密码
    private String password;
    // 邮箱
    private String email;
    // 头像
    private String avatar;
    // 角色
    @TableField(typeHandler = UserRoleEnumTypeHandler.class)
    private UserRoleEnum role;
    // 状态
    private Integer status;
    // 创建时间
    private LocalDateTime createTime;
    // 更新时间
    private LocalDateTime updateTime;
}
