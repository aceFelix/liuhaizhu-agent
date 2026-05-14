package com.itfelix.liuhaizhuaichat.service;

import com.itfelix.liuhaizhuaichat.pojo.vo.ConversationVO;
import com.itfelix.liuhaizhuaichat.pojo.entity.ChatMessage;

import java.util.List;

/**
 * 会话服务接口
 * @author aceFelix
 */
public interface ConversationService {
    /**
     * 创建会话
     * @param userId 用户ID
     * @param title 会话标题
     * @param type 会话类型
     * @return 会话ID
     */
    String createConversation(String userId, String title, String type);
    
    /**
     * 获取用户的会话列表
     * @param userId 用户ID
     * @return 会话列表
     */
    List<ConversationVO> listConversations(String userId);
    
    /**
     * 获取会话详情（包含消息历史）
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 消息列表
     */
    List<ChatMessage> getConversationDetail(String conversationId, String userId);
    
    /**
     * 删除会话
     * @param conversationId 会话ID
     * @param userId 用户ID
     */
    void deleteConversation(String conversationId, String userId);
    
    /**
     * 清空会话消息
     * @param conversationId 会话ID
     * @param userId 用户ID
     */
    void clearConversationMessages(String conversationId, String userId);
    
    /**
     * 保存用户消息
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @param content 消息内容
     * @param messageId 消息ID
     */
    void saveUserMessage(String conversationId, String userId, String content, String messageId);
    
    /**
     * 保存AI回复消息
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @param content 消息内容
     * @param messageId 消息ID
     * @param tokenCount Token消耗数
     */
    void saveAssistantMessage(String conversationId, String userId, String content, String messageId, Integer tokenCount);
    
    /**
     * 更新会话标题
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @param title 新标题
     */
    void updateConversationTitle(String conversationId, String userId, String title);
    
    /**
     * 置顶/取消置顶会话
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @param pinned 是否置顶：true-置顶, false-取消置顶
     */
    void updateConversationPinned(String conversationId, String userId, boolean pinned);
    
    /**
     * 批量删除会话
     * @param conversationIds 会话ID列表
     * @param userId 用户ID
     */
    void batchDeleteConversations(List<String> conversationIds, String userId);
}
