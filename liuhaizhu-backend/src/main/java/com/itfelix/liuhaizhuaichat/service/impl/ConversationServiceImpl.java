package com.itfelix.liuhaizhuaichat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itfelix.liuhaizhuaichat.mapper.ChatMessageMapper;
import com.itfelix.liuhaizhuaichat.mapper.ConversationMapper;
import com.itfelix.liuhaizhuaichat.mapper.TokenUsageMapper;
import com.itfelix.liuhaizhuaichat.pojo.vo.ConversationVO;
import com.itfelix.liuhaizhuaichat.pojo.entity.ChatMessage;
import com.itfelix.liuhaizhuaichat.pojo.entity.Conversation;
import com.itfelix.liuhaizhuaichat.pojo.entity.TokenUsage;
import com.itfelix.liuhaizhuaichat.service.ConversationService;
import com.itfelix.liuhaizhuaichat.service.TitleGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 会话服务实现
 * @author aceFelix
 */
@Slf4j
@Service
public class ConversationServiceImpl implements ConversationService {
    
    @Autowired
    private ConversationMapper conversationMapper;
    
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    
    @Autowired
    private TitleGenerationService titleGenerationService;

    @Autowired
    private TokenUsageMapper tokenUsageMapper;
    
    /**
     * 每个用户最大会话数量
     */
    @Value("${conversation.max-count:50}")
    private int maxConversationCount;

    /**
     * 创建会话
     * @param userId 用户ID
     * @param title 会话标题
     * @param type 会话类型
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createConversation(String userId, String title, String type) {
        // 创建前检查并清理超出数量限制的会话
        cleanupExcessConversations(userId);
        
        Conversation conversation = new Conversation();
        conversation.setUserId(userId);
        conversation.setTitle(title);
        conversation.setType(type);
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setUpdateTime(LocalDateTime.now());
        
        conversationMapper.insert(conversation);
        log.info("创建会话成功，会话ID：{}，用户ID：{}", conversation.getId(), userId);
        
        return conversation.getId();
    }
    
    /**
     * 清理超出数量限制的会话（删除最早未使用的会话）
     * @param userId 用户ID
     */
    private void cleanupExcessConversations(String userId) {
        // 查询用户当前会话数量
        LambdaQueryWrapper<Conversation> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(Conversation::getUserId, userId);
        Long currentCount = conversationMapper.selectCount(countWrapper);
        
        // 如果未达到上限，无需清理
        if (currentCount < maxConversationCount) {
            return;
        }
        
        // 计算需要删除的数量（当前数量 - 上限 + 1，+1是为即将创建的新会话腐出空间）
        int deleteCount = (int) (currentCount - maxConversationCount + 1);
        
        // 查询最早未使用的会话（按 update_time 升序，取前 N 条）
        LambdaQueryWrapper<Conversation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Conversation::getUserId, userId)
                    .orderByAsc(Conversation::getUpdateTime)
                    .last("LIMIT " + deleteCount);
        
        List<Conversation> toDelete = conversationMapper.selectList(queryWrapper);
        
        for (Conversation conv : toDelete) {
            // 删除会话的所有消息
            LambdaQueryWrapper<ChatMessage> msgWrapper = new LambdaQueryWrapper<>();
            msgWrapper.eq(ChatMessage::getConversationId, conv.getId());
            int deletedMsgCount = chatMessageMapper.delete(msgWrapper);
            
            // 删除会话
            conversationMapper.deleteById(conv.getId());
            
            log.info("自动清理超出数量的会话，会话ID：{}，标题：{}，删除消息数：{}", 
                    conv.getId(), conv.getTitle(), deletedMsgCount);
        }
        
        log.info("用户会话数量超限清理完成，用户ID：{}，删除会话数：{}", userId, toDelete.size());
    }

    /**
     * 获取会话列表
     * 优化：使用单次SQL查询替代原来的N+1查询
     * @param userId 用户ID
     * @return
     */
    @Override
    public List<ConversationVO> listConversations(String userId) {
        // 使用优化后的查询，一次性获取会话列表、消息数量和最后一条消息
        return conversationMapper.selectConversationVOList(userId);
    }

    /**
     * 获取会话详情（含消息历史）
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return
     */
    @Override
    public List<ChatMessage> getConversationDetail(String conversationId, String userId) {
        // 验证会话是否属于该用户
        LambdaQueryWrapper<Conversation> convWrapper = new LambdaQueryWrapper<>();
        convWrapper.eq(Conversation::getId, conversationId)
                   .eq(Conversation::getUserId, userId);
        Conversation conversation = conversationMapper.selectOne(convWrapper);
        
        if (conversation == null) {
            log.warn("会话不存在或无权访问，会话ID：{}，用户ID：{}", conversationId, userId);
            return new ArrayList<>();
        }
        
        // 查询消息列表
        LambdaQueryWrapper<ChatMessage> msgWrapper = new LambdaQueryWrapper<>();
        msgWrapper.eq(ChatMessage::getConversationId, conversationId)
                  .orderByAsc(ChatMessage::getCreateTime);
        
        return chatMessageMapper.selectList(msgWrapper);
    }

    /**
     * 删除会话
     * @param conversationId 会话ID
     * @param userId 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConversation(String conversationId, String userId) {
        // 验证会话是否属于该用户
        LambdaQueryWrapper<Conversation> convWrapper = new LambdaQueryWrapper<>();
        convWrapper.eq(Conversation::getId, conversationId)
                   .eq(Conversation::getUserId, userId);
        Conversation conversation = conversationMapper.selectOne(convWrapper);
        
        if (conversation == null) {
            log.warn("会话不存在或无权访问，会话ID：{}，用户ID：{}", conversationId, userId);
            return;
        }
        
        // 删除该会话的所有消息
        LambdaQueryWrapper<ChatMessage> msgWrapper = new LambdaQueryWrapper<>();
        msgWrapper.eq(ChatMessage::getConversationId, conversationId);
        int deletedMsgCount = chatMessageMapper.delete(msgWrapper);
        log.info("删除会话消息成功，会话ID：{}，删除消息数量：{}", conversationId, deletedMsgCount);
        
        // 删除会话
        conversationMapper.deleteById(conversationId);
        log.info("物理删除会话成功，会话ID：{}，用户ID：{}", conversationId, userId);
    }

    /**
     * 清空会话消息
     * @param conversationId 会话ID
     * @param userId 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearConversationMessages(String conversationId, String userId) {
        // 验证会话是否属于该用户
        LambdaQueryWrapper<Conversation> convWrapper = new LambdaQueryWrapper<>();
        convWrapper.eq(Conversation::getId, conversationId)
                   .eq(Conversation::getUserId, userId);
        Conversation conversation = conversationMapper.selectOne(convWrapper);
        if (conversation == null) {
            log.warn("会话不存在或无权访问，会话ID：{}，用户ID：{}", conversationId, userId);
            return;
        }
        
        // 删除该会话的所有消息
        LambdaQueryWrapper<ChatMessage> msgWrapper = new LambdaQueryWrapper<>();
        msgWrapper.eq(ChatMessage::getConversationId, conversationId);
        chatMessageMapper.delete(msgWrapper);
        
        log.info("清空会话消息成功，会话ID：{}，用户ID：{}", conversationId, userId);
    }

    /**
     * 保存用户消息
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @param content 消息内容
     * @param messageId 消息ID
     */
    @Override
    public void saveUserMessage(String conversationId, String userId, String content, String messageId) {
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversationId);
        message.setUserId(userId);
        message.setRole("user");
        message.setContent(content);
        message.setMessageId(messageId);
        message.setCreateTime(LocalDateTime.now());
        
        chatMessageMapper.insert(message);
        
        // 更新会话的更新时间
        updateConversationUpdateTime(conversationId);
        
        log.info("保存用户消息成功，会话ID：{}，消息ID：{}", conversationId, messageId);
    }

    /**
     * 保存AI回复消息
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @param content 消息内容
     * @param messageId 消息ID
     */
    @Override
    public void saveAssistantMessage(String conversationId, String userId, String content, String messageId, Integer tokenCount) {
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversationId);
        message.setUserId(userId);
        message.setRole("assistant");
        message.setContent(content);
        message.setMessageId(messageId);
        message.setTokenCount(tokenCount);
        message.setCreateTime(LocalDateTime.now());

        chatMessageMapper.insert(message);

        updateConversationUpdateTime(conversationId);

        log.info("保存AI回复消息成功，会话ID：{}，消息ID：{}，Token消耗：{}", conversationId, messageId, tokenCount);

        titleGenerationService.checkAndGenerateTitle(conversationId, userId);

        if (tokenCount != null && tokenCount > 0) {
            recordTokenUsage(userId, tokenCount);
        }
    }

    private void recordTokenUsage(String userId, Integer tokenCount) {
        LocalDate today = LocalDate.now();
        TokenUsage existing = tokenUsageMapper.selectByUserIdAndDate(userId, today);
        if (existing != null) {
            existing.setTokenCount(existing.getTokenCount() + tokenCount);
            tokenUsageMapper.updateById(existing);
        } else {
            TokenUsage usage = new TokenUsage();
            usage.setUserId(userId);
            usage.setUsageDate(today);
            usage.setTokenCount(tokenCount);
            usage.setCreateTime(LocalDateTime.now());
            tokenUsageMapper.insert(usage);
        }
    }

    /**
     * 更新会话标题（手动修改）
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @param title 新标题
     */
    @Override
    public void updateConversationTitle(String conversationId, String userId, String title) {
        LambdaUpdateWrapper<Conversation> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Conversation::getId, conversationId)
               .eq(Conversation::getUserId, userId)
               .set(Conversation::getTitle, title)
                // 标记为手动修改，后续不再自动覆盖
               .set(Conversation::getTitleSource, "MANUAL")
               .set(Conversation::getUpdateTime, LocalDateTime.now());
        
        conversationMapper.update(null, wrapper);
        log.info("手动更新会话标题成功，会话ID：{}，新标题：{}", conversationId, title);
    }
    
    /**
     * 置顶/取消置顶会话
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @param pinned 是否置顶：true-置顶, false-取消置顶
     */
    @Override
    public void updateConversationPinned(String conversationId, String userId, boolean pinned) {
        // 验证会话是否属于该用户
        LambdaQueryWrapper<Conversation> convWrapper = new LambdaQueryWrapper<>();
        convWrapper.eq(Conversation::getId, conversationId)
                   .eq(Conversation::getUserId, userId);
        Conversation conversation = conversationMapper.selectOne(convWrapper);
        
        if (conversation == null) {
            log.warn("会话不存在或无权访问，会话ID：{}，用户ID：{}", conversationId, userId);
            return;
        }
        
        LambdaUpdateWrapper<Conversation> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Conversation::getId, conversationId)
               .set(Conversation::getPinned, pinned ? 1 : 0)
               .set(Conversation::getPinnedTime, pinned ? LocalDateTime.now() : null)
               .set(Conversation::getUpdateTime, LocalDateTime.now());
        
        conversationMapper.update(null, wrapper);
        log.info("{}会话成功，会话ID：{}", pinned ? "置顶" : "取消置顶", conversationId);
    }
    
    /**
     * 批量删除会话
     * @param conversationIds 会话ID列表
     * @param userId 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteConversations(List<String> conversationIds, String userId) {
        if (conversationIds == null || conversationIds.isEmpty()) {
            return;
        }
        
        int deletedCount = 0;
        for (String conversationId : conversationIds) {
            try {
                // 验证会话是否属于该用户
                LambdaQueryWrapper<Conversation> convWrapper = new LambdaQueryWrapper<>();
                convWrapper.eq(Conversation::getId, conversationId)
                           .eq(Conversation::getUserId, userId);
                Conversation conversation = conversationMapper.selectOne(convWrapper);
                
                if (conversation == null) {
                    log.warn("会话不存在或无权访问，会话ID：{}，用户ID：{}", conversationId, userId);
                    continue;
                }
                
                // 删除该会话的所有消息
                LambdaQueryWrapper<ChatMessage> msgWrapper = new LambdaQueryWrapper<>();
                msgWrapper.eq(ChatMessage::getConversationId, conversationId);
                chatMessageMapper.delete(msgWrapper);
                
                // 删除会话
                conversationMapper.deleteById(conversationId);
                deletedCount++;
                
                log.info("批量删除会话成功，会话ID：{}", conversationId);
            } catch (Exception e) {
                log.error("批量删除会话失败，会话ID：{}，错误：{}", conversationId, e.getMessage());
            }
        }
        
        log.info("批量删除会话完成，用户ID：{}，删除数量：{}/{}", userId, deletedCount, conversationIds.size());
    }
    
    /**
     * 更新会话的更新时间
     */
    private void updateConversationUpdateTime(String conversationId) {
        LambdaUpdateWrapper<Conversation> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Conversation::getId, conversationId)
               .set(Conversation::getUpdateTime, LocalDateTime.now());
        conversationMapper.update(null, wrapper);
    }
}
