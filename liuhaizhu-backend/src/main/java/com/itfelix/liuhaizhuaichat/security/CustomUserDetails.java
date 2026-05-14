package com.itfelix.liuhaizhuaichat.security;

import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 自定义用户详情类
 * 实现UserDetails接口，用于封装用户信息
 * @author aceFelix
 */

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private User user;
    /**
     * 获取用户的权限集合
     * 基于用户角色构建权限列表，格式为"ROLE_角色码"
     * @return 用户的权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getCode()));
    }
    /**
     * 获取用户的密码
     * @return 用户的密码
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    /**
     * 获取用户的用户名
     * @return 用户的用户名
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    /**
     * 检查用户账号是否过期
     * 假设所有账号都不会过期
     * @return true表示账号未过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    /**
     * 检查用户账号是否被锁定
     * 假设用户状态为1表示正常，2表示被锁定
     * @return true表示账号未被锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() != 2;
    }
    /**
     * 检查用户凭证（密码）是否过期
     * 假设所有凭证都不会过期
     * @return true表示凭证未过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    /**
     * 检查用户是否启用
     * 假设用户状态为1表示启用，2表示禁用
     * @return true表示用户已启用
     */
    @Override
    public boolean isEnabled() {
        return user.getStatus() == 1;
    }
    /**
     * 获取用户的ID
     * @return 用户的ID
     */
    public String getUserId() {
        return user.getUserId();
    }

    /**
     * 获取用户角色
     * @return 用户角色枚举
     */
    public com.itfelix.liuhaizhuaichat.enums.UserRoleEnum getRole() {
        return user.getRole();
    }
}
