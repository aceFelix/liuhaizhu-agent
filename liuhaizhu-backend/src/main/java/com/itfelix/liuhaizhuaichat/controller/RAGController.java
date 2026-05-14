package com.itfelix.liuhaizhuaichat.controller;

import com.itfelix.liuhaizhuaichat.annotation.RequirePermission;
import com.itfelix.liuhaizhuaichat.pojo.dto.RagDocumentDTO;
import com.itfelix.liuhaizhuaichat.pojo.dto.RagUploadStatusDTO;
import com.itfelix.liuhaizhuaichat.pojo.dto.ChatDTO;
import com.itfelix.liuhaizhuaichat.service.ChatService;
import com.itfelix.liuhaizhuaichat.service.RAGService;
import com.itfelix.liuhaizhuaichat.utils.AceResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 知识库管理
 * 
 * @author aceFelix
 */
@Slf4j
@RestController
@RequestMapping("/api/rag")
public class RAGController {
    
    @Autowired
    private RAGService ragService;
    
    @Autowired
    private ChatService chatService;

    /**
     * 异步上传RAG文档到知识库
     * 文件先上传到OSS，然后异步处理文档解析、分片和向量化
     * 只有管理员和VIP用户可以上传知识库
     * @param userId 用户ID
     * @param file 上传的文件
     * @return 上传状态DTO，包含任务ID用于查询进度
     */
    @PostMapping("/upload")
    @RequirePermission(vip = true)
    public AceResult<RagUploadStatusDTO> uploadRagDocument(@RequestParam("userId") String userId,
                                                            @RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        Resource resource = file.getResource();
        RagUploadStatusDTO status = ragService.uploadRagDocumentAsync(userId, resource, fileName);
        log.info("文档上传任务已创建: userId={}, fileName={}, taskId={}", userId, fileName, status.getTaskId());
        return AceResult.success(status);
    }

    /**
     * 查询文档上传处理状态
     * @param taskId 任务ID
     * @return 上传状态DTO
     */
    @GetMapping("/upload/status/{taskId}")
    @RequirePermission(vip = true)
    public AceResult<RagUploadStatusDTO> getUploadStatus(@PathVariable("taskId") String taskId) {
        RagUploadStatusDTO status = ragService.getUploadStatus(taskId);
        return AceResult.success(status);
    }

    /**
     * 获取知识库文档列表
     * 只有管理员和VIP用户可以查看
     * @param userId 用户ID
     * @return
     */
    @GetMapping("/list")
    @RequirePermission(vip = true)
    public AceResult<List<RagDocumentDTO>> getRagDocumentList(@RequestParam("userId") String userId) {
        List<RagDocumentDTO> list = ragService.getRagDocumentList(userId);
        return AceResult.success(list);
    }

    /**
     * 删除知识库文档
     * 只有管理员和VIP用户可以删除
     * @param userId 用户ID
     * @param fileName
     * @return
     */
    @DeleteMapping("/delete/{fileName}")
    @RequirePermission(vip = true)
    public AceResult<Void> deleteRagDocument(@RequestParam("userId") String userId,
                                              @PathVariable("fileName") String fileName) {
        log.info("删除知识库文档: userId={}, fileName={}", userId, fileName);
        ragService.deleteRagDocument(userId, fileName);
        return AceResult.success();
    }

    /**
     * 批量删除知识库文档
     * 只有管理员和VIP用户可以批量删除
     * @param userId 用户ID
     * @param request 包含文件名列表的请求
     * @return 删除结果
     */
    @PostMapping("/batch-delete")
    @RequirePermission(vip = true)
    public AceResult<Void> batchDeleteRagDocuments(@RequestParam("userId") String userId,
                                                    @RequestBody Map<String, List<String>> request) {
        List<String> fileNames = request.get("fileNames");
        if (fileNames == null || fileNames.isEmpty()) {
            return AceResult.error("没有要删除的文档");
        }

        log.info("用户批量删除知识库文档: userId={}, 数量={}", userId, fileNames.size());

        int successCount = 0;
        int failCount = 0;

        for (String fileName : fileNames) {
            try {
                ragService.deleteRagDocument(userId, fileName);
                successCount++;
                log.info("删除文档成功: userId={}, fileName={}", userId, fileName);
            } catch (Exception e) {
                failCount++;
                log.error("删除文档失败: userId={}, fileName={}, error={}", userId, fileName, e.getMessage());
            }
        }

        log.info("批量删除完成: 成功 {}, 失败 {}", successCount, failCount);

        if (failCount > 0) {
            return AceResult.error("删除完成，成功: " + successCount + "，失败: " + failCount);
        }

        return AceResult.success();
    }

    /**
     * 重命名知识库文档
     * 只有管理员和VIP用户可以重命名
     * @param userId 用户ID
     * @param oldName
     * @param newName
     * @return
     */
    @PutMapping("/rename")
    @RequirePermission(vip = true)
    public AceResult<Void> renameRagDocument(@RequestParam("userId") String userId,
                                              @RequestParam("oldName") String oldName,
                                              @RequestParam("newName") String newName) {
        log.info("重命名知识库文档: userId={}, {} -> {}", userId, oldName, newName);
        ragService.renameRagDocument(userId, oldName, newName);
        return AceResult.success();
    }

    /**
     * 启用知识库搜索
     * @param chatDTO
     */
    @PostMapping(value = "/search", produces = "text/event-stream;charset=UTF-8")
    @RequirePermission
    public void searchRagDocument(@RequestBody ChatDTO chatDTO, HttpServletResponse response) {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        
        String userId = chatDTO.getUserId();
        List<Document> documents = ragService.findRagDocument(userId, chatDTO.getMessage());
        chatService.doChatRagSearch(chatDTO, documents);
    }
}
