package com.itfelix.liuhaizhuaichat.service.impl;

import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;
import com.itfelix.liuhaizhuaichat.mapper.ChatMessageMapper;
import com.itfelix.liuhaizhuaichat.mapper.ConversationMapper;
import com.itfelix.liuhaizhuaichat.mapper.UserMapper;
import com.itfelix.liuhaizhuaichat.pojo.dto.RegisterRequest;
import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import com.itfelix.liuhaizhuaichat.pojo.vo.UserVO;
import com.itfelix.liuhaizhuaichat.service.RAGService;
import com.itfelix.liuhaizhuaichat.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类（管理员使用）
 * 用于管理员对用户进行增删改查操作
 * @author aceFelix
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ConversationMapper conversationMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final RAGService ragService;

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户
     */
    @Override
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户
     */
    @Override
    public User findByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    /**
     * 根据用户ID查找用户
     * @param userId 用户ID
     * @return 用户
     */
    @Override
    public User findById(String userId) {
        return userMapper.selectById(userId);
    }

    /**
     * 注册用户
     * @param request 注册请求
     * @return 用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // 邮箱注册时，邮箱作为用户名；用户名注册时，邮箱可为空
        user.setEmail(request.getEmail());
        user.setAvatar("/images/user-avatar.jpg");
        user.setRole(UserRoleEnum.USER);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
        return user;
    }

    /**
     * 将用户实体转换为用户VO
     * @param user 用户实体
     * @return 用户VO
     */
    @Override
    public UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    // ==================== 管理员操作 ====================

    /**
     * 获取所有用户列表（管理员）
     * @return 用户VO列表
     */
    @Override
    public List<UserVO> getAllUsers() {
        List<User> users = userMapper.selectList(null);
        return users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取用户信息（管理员）
     * @param userId 用户ID
     * @return 用户VO
     */
    @Override
    public UserVO getUserById(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToVO(user);
    }

    /**
     * 删除用户及其所有数据（管理员）
     * @param userId 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String userId) {
        // 1. 删除用户的知识库文档
        try {
            var documents = ragService.getRagDocumentList(userId);
            if (documents != null) {
                for (var doc : documents) {
                    try {
                        ragService.deleteRagDocument(userId, doc.getFileName());
                    } catch (Exception e) {
                        log.warn("删除用户 {} 的知识库文档 {} 失败: {}", userId, doc.getFileName(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取用户 {} 的知识库文档列表失败: {}", userId, e.getMessage());
        }

        // 2. 删除用户的聊天消息
        try {
            chatMessageMapper.deleteByUserId(userId);
        } catch (Exception e) {
            log.warn("删除用户 {} 的聊天消息失败: {}", userId, e.getMessage());
        }

        // 3. 删除用户的会话
        try {
            conversationMapper.deleteByUserId(userId);
        } catch (Exception e) {
            log.warn("删除用户 {} 的会话失败: {}", userId, e.getMessage());
        }

        // 4. 删除用户
        userMapper.deleteById(userId);

        log.info("用户 {} 及其所有数据已删除", userId);
    }

    /**
     * 更新用户状态（管理员）
     * @param userId 用户ID
     * @param status 状态（0-禁用，1-启用）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(String userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("用户 {} 状态已更新为 {}", userId, status);
    }

    /**
     * 更新用户角色（管理员）
     * @param userId 用户ID
     * @param role 角色（USER, VIP, ADMIN）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRole(String userId, String role) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        try {
            user.setRole(UserRoleEnum.valueOf(role));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("无效的角色: " + role);
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("用户 {} 角色已更新为 {}", userId, role);
    }

    /**
     * 管理员创建用户（无需密码和邮箱验证码，默认密码123456）
     * @param username 用户名
     * @param email 邮箱
     * @param role 角色
     * @return 创建的用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createUserByAdmin(String username, String email, String role) {
        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(username);
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        existingUser = userMapper.selectByEmail(email);
        if (existingUser != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(username);
        // 默认密码123456
        user.setPassword(passwordEncoder.encode("123456"));
        user.setEmail(email);
        user.setAvatar("/images/user-avatar.jpg");
        try {
            user.setRole(UserRoleEnum.valueOf(role));
        } catch (IllegalArgumentException e) {
            user.setRole(UserRoleEnum.USER);
        }
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
        log.info("管理员创建用户成功: {}", username);
        return user;
    }
}
