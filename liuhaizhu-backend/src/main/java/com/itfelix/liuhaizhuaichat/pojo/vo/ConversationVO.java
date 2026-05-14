package com.itfelix.liuhaizhuaichat.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话VO（包含最后一条消息）
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationVO {
    // 会话ID
    private String id;
    // 用户ID
    private String userId;
    // 会话标题
    private String title;
    // 会话类型
    private String type;
    // 最后一条消息内容
    private String lastMessage;
    // 消息总数
    private Integer messageCount;
    // 是否置顶：0-不置顶, 1-置顶
    private Integer pinned;
    // 置顶时间
    private LocalDateTime pinnedTime;
    // 创建时间
    private LocalDateTime createTime;
    // 更新时间
    private LocalDateTime updateTime;
}
