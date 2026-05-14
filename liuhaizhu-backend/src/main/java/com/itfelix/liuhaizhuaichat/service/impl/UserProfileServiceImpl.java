package com.itfelix.liuhaizhuaichat.service.impl;

import com.itfelix.liuhaizhuaichat.mapper.ChatMessageMapper;
import com.itfelix.liuhaizhuaichat.mapper.ConversationMapper;
import com.itfelix.liuhaizhuaichat.mapper.UserMapper;
import com.itfelix.liuhaizhuaichat.pojo.dto.BindEmailRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.UpdatePasswordRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.UpdateProfileRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.VerificationCodeRequest;
import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import com.itfelix.liuhaizhuaichat.pojo.vo.UserVO;
import com.itfelix.liuhaizhuaichat.service.RAGService;
import com.itfelix.liuhaizhuaichat.service.UserProfileService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 用户个人信息服务实现类
 * 用于用户自己操作自己的个人信息
 * @author aceFelix
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ConversationMapper conversationMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final RAGService ragService;
    private final StringRedisTemplate stringRedisTemplate;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // 验证码Redis前缀
    private static final String VERIFICATION_CODE_PREFIX = "verify:code:";
    // 邮箱绑定验证码前缀
    private static final String BIND_EMAIL_CODE_PREFIX = "bind:email:code:";
    // 验证码有效期（分钟）
    private static final int VERIFICATION_CODE_EXPIRE_MINUTES = 5;

    /**
     * 获取当前用户信息
     * @param userId 用户ID
     * @return 用户VO
     */
    @Override
    public UserVO getCurrentUser(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToVO(user);
    }

    /**
     * 更新用户个人信息
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新后的用户VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateProfile(String userId, UpdateProfileRequest request) {
        // 1. 获取当前用户
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 检查用户名是否被其他用户使用
        if (!currentUser.getUsername().equals(request.getUsername())) {
            User existUser = userMapper.selectByUsername(request.getUsername());
            if (existUser != null) {
                throw new RuntimeException("用户名已被使用");
            }
        }

        // 3. 检查邮箱是否被其他用户使用
        if (!currentUser.getEmail().equals(request.getEmail())) {
            User existUser = userMapper.selectByEmail(request.getEmail());
            if (existUser != null) {
                throw new RuntimeException("邮箱已被使用");
            }
        }

        // 4. 更新用户信息
        currentUser.setUsername(request.getUsername());
        currentUser.setEmail(request.getEmail());
        if (request.getAvatar() != null) {
            currentUser.setAvatar(request.getAvatar());
        }
        currentUser.setUpdateTime(LocalDateTime.now());

        userMapper.updateById(currentUser);

        log.info("用户 {} 更新个人信息成功", userId);
        return convertToVO(currentUser);
    }

    /**
     * 修改密码
     * @param userId 用户ID
     * @param request 密码修改请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(String userId, UpdatePasswordRequest request) {
        // 1. 获取当前用户
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 验证当前密码
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new RuntimeException("当前密码错误");
        }

        // 3. 验证验证码（使用请求中的邮箱验证）
        String codeKey = VERIFICATION_CODE_PREFIX + request.getEmail();
        String storedCode = stringRedisTemplate.opsForValue().get(codeKey);
        if (storedCode == null) {
            throw new RuntimeException("验证码已过期，请重新获取");
        }
        if (!storedCode.equals(request.getVerificationCode())) {
            throw new RuntimeException("验证码错误");
        }

        // 4. 更新密码
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        currentUser.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(currentUser);

        // 5. 删除已使用的验证码
        stringRedisTemplate.delete(codeKey);

        log.info("用户 {} 修改密码成功", userId);
    }

    /**
     * 发送邮箱验证码
     * @param userId 用户ID
     * @param request 验证码请求
     */
    @Override
    public void sendVerificationCode(String userId, VerificationCodeRequest request) {
        // 1. 获取当前用户
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 生成6位验证码
        String code = generateVerificationCode();

        // 3. 存储到Redis（使用请求中的邮箱作为key）
        String codeKey = VERIFICATION_CODE_PREFIX + request.getEmail();
        stringRedisTemplate.opsForValue().set(
                codeKey,
                code,
                VERIFICATION_CODE_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );

        // 4. 发送邮件验证码
        try {
            sendVerificationEmail(request.getEmail(), code, "修改密码");
            log.info("向邮箱 {} 发送验证码成功，有效期 {} 分钟", request.getEmail(), VERIFICATION_CODE_EXPIRE_MINUTES);
        } catch (MessagingException e) {
            log.error("发送邮件验证码失败: {}", e.getMessage());
            throw new RuntimeException("发送验证码邮件失败，请稍后重试");
        }
    }

    /**
     * 发送邮箱绑定验证码
     * @param userId 用户ID
     * @param request 验证码请求
     */
    @Override
    public void sendBindEmailCode(String userId, VerificationCodeRequest request) {
        // 1. 获取当前用户
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 检查用户是否已绑定邮箱
        if (currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) {
            throw new RuntimeException("您已绑定邮箱，无需重复绑定");
        }

        // 3. 检查邮箱是否已被其他用户使用
        User existUser = userMapper.selectByEmail(request.getEmail());
        if (existUser != null) {
            throw new RuntimeException("该邮箱已被其他用户使用");
        }

        // 4. 生成6位验证码
        String code = generateVerificationCode();

        // 5. 存储到Redis
        String codeKey = BIND_EMAIL_CODE_PREFIX + request.getEmail();
        stringRedisTemplate.opsForValue().set(
                codeKey,
                code,
                VERIFICATION_CODE_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );

        // 6. 发送邮件验证码
        try {
            sendVerificationEmail(request.getEmail(), code, "绑定邮箱");
            log.info("向邮箱 {} 发送绑定验证码成功，有效期 {} 分钟", request.getEmail(), VERIFICATION_CODE_EXPIRE_MINUTES);
        } catch (MessagingException e) {
            log.error("发送邮件验证码失败: {}", e.getMessage());
            throw new RuntimeException("发送验证码邮件失败，请稍后重试");
        }
    }

    /**
     * 绑定邮箱
     * @param userId 用户ID
     * @param request 绑定邮箱请求
     * @return 更新后的用户VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO bindEmail(String userId, BindEmailRequest request) {
        // 1. 获取当前用户
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 检查用户是否已绑定邮箱
        if (currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) {
            throw new RuntimeException("您已绑定邮箱，无需重复绑定");
        }

        // 3. 检查邮箱是否已被其他用户使用
        User existUser = userMapper.selectByEmail(request.getEmail());
        if (existUser != null) {
            throw new RuntimeException("该邮箱已被其他用户使用");
        }

        // 4. 验证验证码
        String codeKey = BIND_EMAIL_CODE_PREFIX + request.getEmail();
        String storedCode = stringRedisTemplate.opsForValue().get(codeKey);
        if (storedCode == null) {
            throw new RuntimeException("验证码已过期，请重新获取");
        }
        if (!storedCode.equals(request.getCode())) {
            throw new RuntimeException("验证码错误");
        }

        // 5. 更新用户邮箱
        currentUser.setEmail(request.getEmail());
        currentUser.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(currentUser);

        // 6. 删除已使用的验证码
        stringRedisTemplate.delete(codeKey);

        log.info("用户 {} 成功绑定邮箱 {}", userId, request.getEmail());

        return convertToVO(currentUser);
    }

    /**
     * 注销账户（删除用户及其所有数据）
     * @param userId 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccount(String userId) {
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
     * 将用户实体转换为用户VO
     * @param user 用户实体
     * @return 用户VO
     */
    private UserVO convertToVO(User user) {
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

    /**
     * 生成6位数字验证码
     * @return 验证码
     */
    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 发送验证码邮件
     * @param toEmail 收件人邮箱
     * @param code 验证码
     * @param purpose 用途说明
     * @throws MessagingException 邮件发送异常
     */
    private void sendVerificationEmail(String toEmail, String code, String purpose) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("AI刘海柱 - " + purpose + "验证码");

        String htmlContent = buildEmailContent(code, purpose);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * 构建邮件内容
     * @param code 验证码
     * @param purpose 用途说明
     * @return HTML格式的邮件内容
     */
    private String buildEmailContent(String code, String purpose) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }\n" +
                "        .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 40px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n" +
                "        .header { text-align: center; margin-bottom: 30px; }\n" +
                "        .header h1 { color: #333333; margin: 0; font-size: 24px; }\n" +
                "        .content { color: #666666; line-height: 1.6; font-size: 16px; }\n" +
                "        .code-box { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; border-radius: 8px; text-align: center; margin: 30px 0; }\n" +
                "        .code { font-size: 32px; font-weight: bold; color: #ffffff; letter-spacing: 8px; }\n" +
                "        .footer { text-align: center; color: #999999; font-size: 14px; margin-top: 30px; }\n" +
                "        .warning { color: #ff6b6b; font-size: 14px; margin-top: 20px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>🤖 AI刘海柱</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>用户您好！</p>\n" +
                "            <p>您的" + purpose + "验证码如下：</p>\n" +
                "            <div class=\"code-box\">\n" +
                "                <span class=\"code\">" + code + "</span>\n" +
                "            </div>\n" +
                "            <p class=\"warning\">验证码有效期为 " + VERIFICATION_CODE_EXPIRE_MINUTES + " 分钟，请勿泄露给他人。</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>如非本人操作，请忽略此邮件。</p>\n" +
                "            <p>艾斯FelixAI团队</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
