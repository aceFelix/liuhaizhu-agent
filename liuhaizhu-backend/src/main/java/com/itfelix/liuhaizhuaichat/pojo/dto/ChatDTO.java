package com.itfelix.liuhaizhuaichat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 聊天实体类
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatDTO {
    // 用户ID
    private String userId;
    // 用户消息
    private String message;
    // 机器人消息ID
    private String botMessageId;
    // 用户消息ID（前端生成）
    private String userMessageId;
    // 会话ID（可选，如果不传则不保存历史）
    private String conversationId;
    // 上传的文件内容（用于文件上传聊天）
    private String fileContent;
    // 上传的文件名
    private String fileName;
}
