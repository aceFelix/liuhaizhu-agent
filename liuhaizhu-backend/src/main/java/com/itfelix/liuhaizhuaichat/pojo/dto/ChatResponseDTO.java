package com.itfelix.liuhaizhuaichat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 大模型响应实体类
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatResponseDTO {
    // 响应消息
    private String message;
    // 响应消息ID
    private String botMessageId;
}
