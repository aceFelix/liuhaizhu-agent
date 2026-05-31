package com.itfelix.liuhaizhuaichat.mcp.tool;

import com.itfelix.liuhaizhuaichat.mcp.bean.EmailRequest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * EmailTool 单元测试
 * @author aceFelix
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmailTool 单元测试")
class EmailToolTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailTool emailTool;

    private static final String FROM_EMAIL = "admin@liuhaizhu.com";

    @BeforeEach
    void setUp() {
        emailTool = new EmailTool(mailSender, FROM_EMAIL);
    }

    @Nested
    @DisplayName("getMyBossEmailAddress - 获取维护者邮箱")
    class GetMyBossEmailAddressTests {

        @Test
        @DisplayName("应返回配置的发件人邮箱")
        void shouldReturnFromEmail() {
            String result = emailTool.getMyBossEmailAddress();
            assertEquals(FROM_EMAIL, result);
        }
    }

    @Nested
    @DisplayName("sendMail - 发送邮件")
    class SendMailTests {

        @Test
        @DisplayName("发送成功应返回成功消息")
        void shouldReturnSuccessMessageWhenSent() {
            when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((jakarta.mail.Session) null));

            EmailRequest request = new EmailRequest();
            request.setEmail("user@example.com");
            request.setSubject("测试主题");
            request.setContent("测试内容");
            request.setContentType(0);

            String result = emailTool.sendMail(request);

            assertTrue(result.contains("user@example.com"));
            assertTrue(result.contains("成功"));
            verify(mailSender).send(any(MimeMessage.class));
        }

        @Test
        @DisplayName("发送失败时 RuntimeException 会向上传播（sendMail 只捕获 MessagingException）")
        void shouldPropagateRuntimeException() {
            when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((jakarta.mail.Session) null));
            doThrow(new RuntimeException("Connection refused")).when(mailSender).send(any(MimeMessage.class));

            EmailRequest request = new EmailRequest();
            request.setEmail("user@example.com");
            request.setSubject("测试");
            request.setContent("内容");
            request.setContentType(0);

            assertThrows(RuntimeException.class, () -> emailTool.sendMail(request));
        }
    }
}
