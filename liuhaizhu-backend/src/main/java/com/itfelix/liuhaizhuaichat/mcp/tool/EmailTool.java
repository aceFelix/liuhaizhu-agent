package com.itfelix.liuhaizhuaichat.mcp.tool;

import com.itfelix.liuhaizhuaichat.mcp.bean.EmailRequest;
import com.itfelix.liuhaizhuaichat.mcp.utils.DocumentFormatConversion;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * @author aceFelix
 */
@Component
@Slf4j
public class EmailTool {
    private final JavaMailSender mailSender;
    private final String from;
    @Autowired
    public EmailTool(JavaMailSender mailSender, @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Autowired
    private DocumentFormatConversion documentFormatConversion;

    @Tool(description = "给指定邮箱发送邮件，email是收件人邮箱，subject是邮件标题，content是邮件的内容")
    public String sendMail(EmailRequest emailRequest){
        log.info("===================给指定邮箱发送邮件sendMail===================");
        log.info("收件人邮箱：{}", emailRequest.getEmail());
        log.info("邮件标题：{}", emailRequest.getSubject());
        log.info("邮件内容：{}", emailRequest.getContent());

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(emailRequest.getEmail());
            helper.setSubject(emailRequest.getSubject());
            if (emailRequest.getContentType() == 1){
                helper.setText(documentFormatConversion.convertToHtml(emailRequest.getContent()), true);
            } else if (emailRequest.getContentType() == 2) {
                helper.setText(emailRequest.getContent(), true);
            } else {
                helper.setText(emailRequest.getContent());
            }
            mailSender.send(mimeMessage);
            log.info("邮件发送成功！");
            return "邮件已成功发送到 " + emailRequest.getEmail();
        } catch (MessagingException e) {
            log.error("邮件发送失败：{}", e.getMessage());
            return "邮件发送失败：" + e.getMessage();
        }
    }

    @Tool(description = " 查询项目维护者的邮箱地址 ")
    public String getMyBossEmailAddress(){
        log.info("===================查询项目维护者的邮箱地址getMyBossEmailAddress===================");
        return from;
    }
}