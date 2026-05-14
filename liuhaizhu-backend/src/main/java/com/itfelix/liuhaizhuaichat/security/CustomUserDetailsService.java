package com.itfelix.liuhaizhuaichat.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itfelix.liuhaizhuaichat.mapper.UserMapper;
import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 自定义用户详情服务类
 * 实现UserDetailsService接口，用于加载用户信息
 * @author aceFelix
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    /**
     * 根据用户名加载用户详情
     * 从数据库中查询用户信息，并封装为CustomUserDetails对象返回
     * @param username 用户名
     * @return 包含用户详情的CustomUserDetails对象
     * @throws UsernameNotFoundException 如果用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        return new CustomUserDetails(user);
    }
}
