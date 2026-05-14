package com.itfelix.liuhaizhuaichat.initializer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;
import com.itfelix.liuhaizhuaichat.mapper.UserMapper;
import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据初始化器
 * 在应用启动时进行数据初始化，确保数据库中存在默认的管理员用户
 * @author aceFelix
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initDefaultAdmin();
    }

    private void initDefaultAdmin() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, "admin");
        User existingAdmin = userMapper.selectOne(queryWrapper);

        if (existingAdmin == null) {
            User admin = new User();
            admin.setUserId("1");
            admin.setUsername("admin");
            // 密码使用密码编码器进行编码，默认加密方式为BCrypt
            admin.setPassword(passwordEncoder.encode("ChangeMe123456"));
            admin.setEmail("admin@example.com");
            admin.setAvatar("/images/user-avatar.jpg");
            admin.setRole(UserRoleEnum.ADMIN);
            admin.setStatus(1);
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());

            userMapper.insert(admin);
            log.info("默认管理员用户初始化成功: admin");
        } else {
            log.info("默认管理员用户已存在: admin");
        }
    }
}
