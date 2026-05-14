package com.itfelix.liuhaizhuaichat.service;

import com.itfelix.liuhaizhuaichat.pojo.dto.RagDocumentDTO;
import com.itfelix.liuhaizhuaichat.pojo.dto.RagUploadStatusDTO;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * @author aceFelix
 */
public interface RAGService {
    /**
     * 异步上传RAG文档
     * 文件先上传到OSS，然后异步处理文档解析、分片和向量化
     * @param userId 用户ID
     * @param resource 文件资源
     * @param fileName 文件名
     * @return 上传状态DTO，包含任务ID用于查询进度
     */
    RagUploadStatusDTO uploadRagDocumentAsync(String userId, Resource resource, String fileName);

    /**
     * 查询文档上传处理状态
     * @param taskId 任务ID
     * @return 上传状态DTO
     */
    RagUploadStatusDTO getUploadStatus(String taskId);

    /**
     * 根据用户的提问从知识库中查找相似的知识
     * @param userId 用户ID
     * @param question
     * @return
     */
    List<Document> findRagDocument(String userId, String question);

    /**
     * 获取知识库文档列表
     * @param userId 用户ID
     * @return
     */
    List<RagDocumentDTO> getRagDocumentList(String userId);

    /**
     * 删除知识库文档
     * @param userId 用户ID
     * @param fileName
     */
    void deleteRagDocument(String userId, String fileName);

    /**
     * 重命名知识库文档
     * @param userId 用户ID
     * @param oldName
     * @param newName
     */
    void renameRagDocument(String userId, String oldName, String newName);

    /**
     * 批量删除知识库文档
     * @param userId 用户ID
     * @param fileNames 文件名列表
     */
    void batchDeleteRagDocuments(String userId, List<String> fileNames);

    /**
     * 获取文档内容
     * @param userId 用户ID
     * @param fileName 文件名
     * @return 文档内容字节数组
     */
    byte[] getDocumentContent(String userId, String fileName);
}
