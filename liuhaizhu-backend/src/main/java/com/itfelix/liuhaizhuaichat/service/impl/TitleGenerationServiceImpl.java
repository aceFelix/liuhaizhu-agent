package com.itfelix.liuhaizhuaichat.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itfelix.liuhaizhuaichat.enums.SSEMessageType;
import com.itfelix.liuhaizhuaichat.mapper.ChatMessageMapper;
import com.itfelix.liuhaizhuaichat.mapper.ConversationMapper;
import com.itfelix.liuhaizhuaichat.pojo.entity.ChatMessage;
import com.itfelix.liuhaizhuaichat.pojo.entity.Conversation;
import com.itfelix.liuhaizhuaichat.service.TitleGenerationService;
import com.itfelix.liuhaizhuaichat.utils.SSEServerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 会话标题生成服务实现
 * @author aceFelix
 */
@Slf4j
@Service
public class TitleGenerationServiceImpl implements TitleGenerationService {
    
    @Autowired
    private ConversationMapper conversationMapper;
    
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    
    @Autowired
    @Qualifier("qwen3-max")
    private ChatClient chatClient;
    
    // 标题生成提示词
    private static final String TITLE_GENERATION_PROMPT = """
            你是一个会话标题生成助手。根据以下对话内容，生成一个简洁的中文标题。
            
            要求：
            1. 标题长度不超过20个字
            2. 准确概括对话主题
            3. 只输出标题文本，不要任何其他内容、标点或解释
            
            对话内容：
            %s
            """;
    
    /**
     * 检查并生成会话标题（异步执行）
     * @param conversationId 会话ID
     * @param userId 用户ID
     */
    @Async
    @Override
    public void checkAndGenerateTitle(String conversationId, String userId) {
        try {
            // 1. 查询会话信息
            Conversation conversation = conversationMapper.selectById(conversationId);
            if (conversation == null) {
                log.warn("会话不存在，会话ID：{}", conversationId);
                return;
            }
            
            // 2. 如果是手动修改的标题，不再自动覆盖
            if ("MANUAL".equals(conversation.getTitleSource())) {
                log.info("会话标题为手动修改，跳过自动生成，会话ID：{}", conversationId);
                return;
            }
            
            // 3. 查询消息数量，计算对话轮数
            LambdaQueryWrapper<ChatMessage> countWrapper = new LambdaQueryWrapper<>();
            countWrapper.eq(ChatMessage::getConversationId, conversationId);
            Long messageCount = chatMessageMapper.selectCount(countWrapper);
            // 一轮 = 用户消息 + AI回复
            int roundCount = (int) (messageCount / 2);
            
            // 4. 判断是否需要生成标题
            Integer titleGeneratedAt = conversation.getTitleGeneratedAt();
            if (titleGeneratedAt == null) {
                titleGeneratedAt = 0;
            }
            
            boolean shouldGenerate = false;
            
            // 规则1：两轮对话后首次生成
            if (roundCount >= 2 && titleGeneratedAt == 0) {
                shouldGenerate = true;
                log.info("触发标题生成：两轮对话后首次生成，会话ID：{}，当前轮数：{}", conversationId, roundCount);
            }
            // 规则2：五轮对话时更新一次（且之前是在第2轮生成的）
            else if (roundCount >= 5 && titleGeneratedAt > 0 && titleGeneratedAt < 5) {
                shouldGenerate = true;
                log.info("触发标题生成：五轮对话更新，会话ID：{}，当前轮数：{}", conversationId, roundCount);
            }
            
            if (!shouldGenerate) {
                return;
            }
            
            // 5. 获取对话内容（最多取前5轮）
            LambdaQueryWrapper<ChatMessage> msgWrapper = new LambdaQueryWrapper<>();
            msgWrapper.eq(ChatMessage::getConversationId, conversationId)
                      .orderByAsc(ChatMessage::getCreateTime)
                      // 5轮 = 10条消息
                      .last("LIMIT 10");
            
            List<ChatMessage> messages = chatMessageMapper.selectList(msgWrapper);
            if (messages.isEmpty()) {
                return;
            }
            
            // 6. 构建对话内容字符串
            String dialogContent = messages.stream()
                    .map(msg -> String.format("[%s]: %s", 
                            "user".equals(msg.getRole()) ? "用户" : "助手",
                            msg.getContent().length() > 200 ? msg.getContent().substring(0, 200) + "..." : msg.getContent()))
                    .collect(Collectors.joining("\n"));
            
            // 7. 调用LLM生成标题
            String prompt = String.format(TITLE_GENERATION_PROMPT, dialogContent);
            String generatedTitle = chatClient.prompt(prompt).call().content();
            
            // 清理标题（去除可能的引号、换行等）
            generatedTitle = generatedTitle.trim()
                    .replaceAll("^[\"'「]|[\"'」]$", "")
                    .replaceAll("\n", "");
            
            // 限制标题长度
            if (generatedTitle.length() > 20) {
                generatedTitle = generatedTitle.substring(0, 20);
            }
            
            log.info("生成会话标题成功，会话ID：{}，新标题：{}", conversationId, generatedTitle);
            
            // 8. 更新会话标题
            LambdaUpdateWrapper<Conversation> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Conversation::getId, conversationId)
                         .set(Conversation::getTitle, generatedTitle)
                         .set(Conversation::getTitleSource, "AUTO")
                         .set(Conversation::getTitleGeneratedAt, roundCount)
                         .set(Conversation::getUpdateTime, LocalDateTime.now());
            conversationMapper.update(null, updateWrapper);
            
            // 9. 通过SSE推送新标题给前端
            Map<String, String> titleUpdateData = new HashMap<>();
            titleUpdateData.put("conversationId", conversationId);
            titleUpdateData.put("title", generatedTitle);
            SSEServerUtil.sendMessage(userId, JSONUtil.toJsonStr(titleUpdateData), SSEMessageType.TITLE_UPDATE);
            
            log.info("已通过SSE推送新标题，用户ID：{}，会话ID：{}", userId, conversationId);
            
        } catch (Exception e) {
            log.error("生成会话标题异常，会话ID：{}，错误：{}", conversationId, e.getMessage(), e);
        }
    }
}
