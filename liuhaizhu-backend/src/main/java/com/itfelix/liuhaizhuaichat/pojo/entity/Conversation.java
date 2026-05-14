package com.itfelix.liuhaizhuaichat.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话实体类
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("conversation")
public class Conversation {
    // 会话ID
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 用户ID
    private String userId;
    // 会话标题
    private String title;
    // 标题来源：AUTO-AI生成, MANUAL-手动修改
    private String titleSource;
    // 标题生成时的对话轮数
    private Integer titleGeneratedAt;
    // 会话类型：normal-普通对话, rag-知识库对话, web_search-联网搜索
    private String type;
    // 是否置顶：0-不置顶, 1-置顶
    private Integer pinned;
    // 置顶时间
    private LocalDateTime pinnedTime;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
