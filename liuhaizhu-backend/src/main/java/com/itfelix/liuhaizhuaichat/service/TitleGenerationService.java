package com.itfelix.liuhaizhuaichat.service;

/**
 * 会话标题生成服务接口
 * @author aceFelix
 */
public interface TitleGenerationService {
    
    /**
     * 检查并生成会话标题（异步执行）
     * 触发规则：
     * 1. 两轮对话后首次生成
     * 2. 五轮对话时更新一次
     * 3. 用户手动修改后不再自动覆盖
     * 
     * @param conversationId 会话ID
     * @param userId 用户ID
     */
    void checkAndGenerateTitle(String conversationId, String userId);
}
