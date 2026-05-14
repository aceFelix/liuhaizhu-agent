package com.itfelix.liuhaizhuaichat.mcp.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 邮件请求参数
 * @author aceFelix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    @ToolParam(description = "收件人邮箱")
    private String email;
    @ToolParam(description = "发送邮件的主题")
    private String subject;
    @ToolParam(description = "发送邮件的内容")
    private String content;
    @ToolParam(description = "邮件内容类型，1为MarkDown，2为HTML")
    private Integer contentType;
}
