package com.itfelix.liuhaizhuaichat.controller;

import com.itfelix.liuhaizhuaichat.annotation.RequirePermission;
import com.itfelix.liuhaizhuaichat.pojo.vo.ConversationVO;
import com.itfelix.liuhaizhuaichat.pojo.entity.ChatMessage;
import com.itfelix.liuhaizhuaichat.service.ConversationService;
import com.itfelix.liuhaizhuaichat.utils.AceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 会话管理Controller
 * 
 * @author aceFelix
 */
@RestController
@RequestMapping("/api/conversation")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    /**
     * 创建会话
     * 
     * @param params
     */
    @PostMapping("/create")
    @RequirePermission
    public AceResult<String> createConversation(@RequestBody Map<String, String> params) {
        String userId = params.get("userId");
        String title = params.getOrDefault("title", "新对话");
        String type = params.getOrDefault("type", "normal");

        String conversationId = conversationService.createConversation(userId, title, type);
        return AceResult.success(conversationId);
    }

    /**
     * 获取会话列表
     * 
     * @param userId
     */
    @GetMapping("/list")
    @RequirePermission
    public AceResult<List<ConversationVO>> listConversations(@RequestParam String userId) {
        List<ConversationVO> conversations = conversationService.listConversations(userId);
        return AceResult.success(conversations);
    }

    /**
     * 获取会话详情（含消息历史）
     * 
     * @param conversationId
     */
    @GetMapping("/detail")
    @RequirePermission
    public AceResult<List<ChatMessage>> getConversationDetail(@RequestParam String conversationId,
            @RequestParam String userId) {
        List<ChatMessage> messages = conversationService.getConversationDetail(conversationId, userId);
        return AceResult.success(messages);
    }

    /**
     * 删除会话
     * 
     * @param conversationId
     */
    @DeleteMapping("/delete")
    @RequirePermission
    public AceResult<Void> deleteConversation(@RequestParam String conversationId,
            @RequestParam String userId) {
        conversationService.deleteConversation(conversationId, userId);
        return AceResult.success();
    }

    /**
     * 更新会话标题
     */
    @PutMapping("/updateTitle")
    @RequirePermission
    public AceResult<Void> updateConversationTitle(@RequestBody Map<String, Object> params) {
        String conversationId = params.get("conversationId").toString();
        String userId = params.get("userId").toString();
        String title = params.get("title").toString();

        conversationService.updateConversationTitle(conversationId, userId, title);
        return AceResult.success();
    }
    
    /**
     * 置顶/取消置顶会话
     */
    @PutMapping("/pin")
    @RequirePermission
    public AceResult<Void> updateConversationPinned(@RequestBody Map<String, Object> params) {
        String conversationId = params.get("conversationId").toString();
        String userId = params.get("userId").toString();
        Boolean pinned = Boolean.valueOf(params.get("pinned").toString());

        conversationService.updateConversationPinned(conversationId, userId, pinned);
        return AceResult.success();
    }
    
    /**
     * 批量删除会话
     */
    @DeleteMapping("/batchDelete")
    @RequirePermission
    public AceResult<Void> batchDeleteConversations(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<String> conversationIds = (List<String>) params.get("conversationIds");
        String userId = params.get("userId").toString();

        conversationService.batchDeleteConversations(conversationIds, userId);
        return AceResult.success();
    }
}
