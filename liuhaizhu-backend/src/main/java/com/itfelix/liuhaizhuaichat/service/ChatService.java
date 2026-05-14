package com.itfelix.liuhaizhuaichat.service;

import com.itfelix.liuhaizhuaichat.pojo.dto.ChatDTO;
import org.springframework.ai.document.Document;

import java.util.List;

/**
 * @author aceFelix
 */
public interface ChatService {
    /**
     * 与大模型交互
     * @param chatDTO
     */
    void doChat(ChatDTO chatDTO);

    /**
     * 与大模型交互（启用RAG搜索）
     * @param chatDTO
     * @param ragContext
     */
    void doChatRagSearch(ChatDTO chatDTO, List<Document> ragContext);

    /**
     * 与大模型交互（启用联网搜索）
     * @param chatDTO
     */
    void doChatWebSearch(ChatDTO chatDTO);

    /**
     * 与大模型交互（基于上传的文件内容）
     * @param chatDTO
     */
    void doChatWithFile(ChatDTO chatDTO);
}
