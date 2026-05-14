package com.itfelix.liuhaizhuaichat.controller;

import com.itfelix.liuhaizhuaichat.annotation.RequirePermission;
import com.itfelix.liuhaizhuaichat.pojo.dto.ChatDTO;
import com.itfelix.liuhaizhuaichat.service.ChatService;
import com.itfelix.liuhaizhuaichat.service.ConversationService;
import com.itfelix.liuhaizhuaichat.utils.AceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 聊天Controller
 * 
 * @author aceFelix
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @Autowired
    private ConversationService conversationService;

    /**
     * 与大模型交互聊天
     *
     * @param chatDTO 聊天实体
     */
    @PostMapping("doChat")
    public void doChat(@RequestBody ChatDTO chatDTO) {
        chatService.doChat(chatDTO);
    }

    /**
     * 清空会话消息
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     */
    @DeleteMapping("/conversations/clear")
    @RequirePermission
    public AceResult<Void> clearConversationMessages(@RequestParam String conversationId,
            @RequestParam String userId) {
        conversationService.clearConversationMessages(conversationId, userId);
        return AceResult.success();
    }
}
