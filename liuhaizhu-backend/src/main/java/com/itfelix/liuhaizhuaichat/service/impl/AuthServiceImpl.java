package com.itfelix.liuhaizhuaichat.service.impl;

import com.itfelix.liuhaizhuaichat.annotation.DistributedLock;
import com.itfelix.liuhaizhuaichat.pojo.dto.LoginRequest;
import com.itfelix.liuhaizhuaichat.pojo.dto.RegisterRequest;
import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import com.itfelix.liuhaizhuaichat.pojo.vo.LoginResponse;
import com.itfelix.liuhaizhuaichat.pojo.vo.UserVO;
import com.itfelix.liuhaizhuaichat.service.AuthService;
import com.itfelix.liuhaizhuaichat.service.AdminUserService;
import com.itfelix.liuhaizhuaichat.utils.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 * @author aceFelix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final AdminUserService adminUserService;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // 验证码Redis前缀
    private static final String VERIFICATION_CODE_PREFIX = "verify:code:";
    // 注册验证码前缀
    private static final String REGISTER_CODE_PREFIX = "register:code:";
    // 验证码有效期（分钟）
    private static final int CODE_EXPIRE_MINUTES = 5;

    /**
     * 登录
     * @param request 登录请求
     * @return 登录响应
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            // 执行认证
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // 查询用户信息
            User user = adminUserService.findByUsername(request.getUsername());
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            // 生成token
            return buildLoginResponse(user);
        } catch (BadCredentialsException e) {
            log.warn("登录失败，用户名或密码错误: {}", request.getUsername());
            throw new RuntimeException("用户名或密码错误");
        } catch (AuthenticationException e) {
            log.error("认证异常: {}", e.getMessage());
            throw new RuntimeException("认证失败，请稍后重试");
        }
    }
    /**
     * 注册
     * 使用分布式锁防止并发注册同一用户名
     * @param request 注册请求
     * @return 登录响应
     */
    @Override
    @DistributedLock(key = "'register:' + #request.username", leaseTime = 10, message = "注册请求处理中，请勿重复提交")
    public LoginResponse register(RegisterRequest request) {
        log.info("开始注册用户: username={}", request.getUsername());
        
        // 检查用户名是否已被注册
        if (adminUserService.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已被注册");
        }

        // 根据注册方式处理
        if ("email".equals(request.getRegisterMode())) {
            // 邮箱注册
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                throw new RuntimeException("邮箱不能为空");
            }
            if (request.getCode() == null || request.getCode().isEmpty()) {
                throw new RuntimeException("验证码不能为空");
            }
            // 检查邮箱是否已被注册
            if (adminUserService.findByEmail(request.getEmail()) != null) {
                throw new RuntimeException("邮箱已被注册");
            }
            // 验证验证码
            String codeKey = REGISTER_CODE_PREFIX + request.getEmail();
            String storedCode = stringRedisTemplate.opsForValue().get(codeKey);
            if (storedCode == null) {
                throw new RuntimeException("验证码已过期，请重新获取");
            }
            if (!storedCode.equals(request.getCode())) {
                throw new RuntimeException("验证码错误");
            }
            // 删除已使用的验证码
            stringRedisTemplate.delete(codeKey);
        } else if ("username".equals(request.getRegisterMode())) {
            // 用户名注册，邮箱可为空
            // 如果提供了邮箱，检查是否已被注册
            if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                if (adminUserService.findByEmail(request.getEmail()) != null) {
                    throw new RuntimeException("邮箱已被注册");
                }
            }
        } else {
            throw new RuntimeException("无效的注册方式");
        }

        // 注册用户
        User user = adminUserService.register(request);

        // 生成token并返回
        return buildLoginResponse(user);
    }
    /**
     * 刷新token
     * @param refreshToken 刷新token
     * @return 登录响应
     */
    @Override
    public LoginResponse refreshToken(String refreshToken) {
        // 验证refreshToken
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("刷新token无效或已过期");
        }

        // 获取用户信息
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        User user = adminUserService.findById(userId);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 生成新的token
        return buildLoginResponse(user);
    }
    /**
     * 获取当前用户信息（通过用户名）
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public UserVO getCurrentUser(String username) {
        if (username == null) {
            throw new RuntimeException("未登录");
        }

        User user = adminUserService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        return adminUserService.convertToVO(user);
    }
    /**
     * 获取当前用户信息（通过用户ID）
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public UserVO getCurrentUserById(String userId) {
        if (userId == null) {
            throw new RuntimeException("未登录");
        }

        User user = adminUserService.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        return adminUserService.convertToVO(user);
    }
    /**
     * 删除账户（注销用户）
     * @param username 用户名
     */
    @Override
    public void deleteAccount(String username) {
        if (username == null) {
            throw new RuntimeException("未登录");
        }
        
        User user = adminUserService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 删除用户及其相关数据
        adminUserService.deleteUser(user.getUserId());
        log.info("用户 {} 已注销账户", username);
    }
    /**
     * 删除账户（注销用户）- 通过用户ID
     * @param userId 用户ID
     */
    @Override
    public void deleteAccountById(String userId) {
        if (userId == null) {
            throw new RuntimeException("未登录");
        }
        
        User user = adminUserService.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 删除用户及其相关数据
        adminUserService.deleteUser(userId);
        log.info("用户 {} 已注销账户", user.getUsername());
    }

    /**
     * 发送注册验证码
     * @param email 邮箱
     */
    @Override
    public void sendRegisterVerificationCode(String email) {
        // 检查邮箱是否已被注册
        if (adminUserService.findByEmail(email) != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 生成6位验证码
        String code = generateVerificationCode();

        // 存储到Redis
        String codeKey = REGISTER_CODE_PREFIX + email;
        stringRedisTemplate.opsForValue().set(
                codeKey,
                code,
                CODE_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );

        // 发送邮件验证码
        try {
            sendVerificationEmail(email, code);
            log.info("向邮箱 {} 发送注册验证码成功，有效期 {} 分钟", email, CODE_EXPIRE_MINUTES);
        } catch (MessagingException e) {
            log.error("发送邮件验证码失败: {}", e.getMessage());
            throw new RuntimeException("发送验证码邮件失败，请稍后重试");
        }
    }

    /**
     * 升级用户为VIP
     * @param userId 用户ID
     */
    @Override
    public void upgradeToVip(String userId) {
        if (userId == null) {
            throw new RuntimeException("未登录");
        }
        
        User user = adminUserService.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查用户是否已经是VIP或管理员
        if ("VIP".equals(user.getRole().getCode()) || "ADMIN".equals(user.getRole().getCode())) {
            throw new RuntimeException("您已经是VIP会员");
        }
        
        // 升级用户角色为VIP
        adminUserService.updateUserRole(userId, "VIP");
        log.info("用户 {} 成功升级为VIP", user.getUsername());
    }

    /**
     * 发送验证码邮件
     * @param toEmail 收件人邮箱
     * @param code 验证码
     * @throws MessagingException 邮件发送异常
     */
    private void sendVerificationEmail(String toEmail, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("AI刘海柱 - 注册验证码");

        String htmlContent = buildEmailContent(code);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * 构建邮件内容
     * @param code 验证码
     * @return HTML格式的邮件内容
     */
    private String buildEmailContent(String code) {
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
                "            <p>感谢您注册刘海柱AI智能助手。您的验证码如下：</p>\n" +
                "            <div class=\"code-box\">\n" +
                "                <span class=\"code\">" + code + "</span>\n" +
                "            </div>\n" +
                "            <p class=\"warning\">验证码有效期为 " + CODE_EXPIRE_MINUTES + " 分钟，请勿泄露给他人。</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>如非本人操作，请忽略此邮件。</p>\n" +
                "            <p>艾斯FelixAI团队</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
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
     * 构建登录响应
     * @param user 用户实体
     * @return 登录响应
     */
    private LoginResponse buildLoginResponse(User user) {
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole().getCode());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setUser(adminUserService.convertToVO(user));

        return response;
    }
}
