package com.itfelix.liuhaizhuaichat.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("chat_message")
public class ChatMessage {
    // 消息ID
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 会话ID
    private String conversationId;
    // 用户ID
    private String userId;
    // 消息角色：user-用户, assistant-助手
    private String role;
    // 消息内容
    private String content;
    // 消息ID（前端生成的唯一标识）
    private String messageId;
    // 创建时间
    private LocalDateTime createTime;
    // Token消耗
    private Integer tokenCount;
}
