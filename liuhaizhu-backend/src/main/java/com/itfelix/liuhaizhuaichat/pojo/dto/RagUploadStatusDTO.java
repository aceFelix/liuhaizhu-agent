package com.itfelix.liuhaizhuaichat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RAG文档上传状态DTO
 * 用于异步上传时返回处理状态
 * 使用场景：
 * 1. 用户上传文档后，立即返回一个taskId，客户端可以通过taskId轮询查询处理进度
 * 2. 支持大文件分片处理，实时反馈处理进度
 * 3. 处理失败时返回详细的错误信息
 * 
 * @author aceFelix
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagUploadStatusDTO {
    
    /**
     * 处理状态枚举
     * PENDING: 待处理 - 文件已上传，等待系统开始处理
     * PROCESSING: 处理中 - 正在对文档进行分片和向量化
     * COMPLETED: 已完成 - 文档处理成功，可以进行RAG检索
     * FAILED: 失败 - 处理过程中出现错误
     */
    public enum Status {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
    
    /** 任务唯一标识，用于查询处理状态 */
    private String taskId;
    
    /** 上传的文件名 */
    private String fileName;
    
    /** 当前处理状态 */
    private Status status;
    
    /** 处理进度百分比（0-100） */
    private Integer progress;
    
    /** 状态描述信息，用于前端展示 */
    private String message;
    
    /** 文档被分割的总片段数 */
    private Integer totalChunks;
    
    /** 已处理的片段数 */
    private Integer processedChunks;
    
    /** 文件大小（字节） */
    private Long fileSize;
    
    /** 错误信息，仅当status=FAILED时有值 */
    private String errorMessage;
    
    /** 任务创建时间戳（毫秒） */
    private Long createdAt;
    
    /** 任务完成时间戳（毫秒），仅当status=COMPLETED时有值 */
    private Long completedAt;

    /**
     * 创建待处理状态的DTO
     * @param taskId 任务唯一标识，用于后续查询状态
     * @param fileName 上传的文件名
     * @param fileSize 文件大小（字节）
     * @return 待处理状态的DTO对象
     */
    public static RagUploadStatusDTO pending(String taskId, String fileName, Long fileSize) {
        return RagUploadStatusDTO.builder()
                .taskId(taskId)
                .fileName(fileName)
                .status(Status.PENDING)
                .progress(0)
                .message("文件已上传，等待处理")
                .fileSize(fileSize)
                .createdAt(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建处理中状态的DTO
     * @param taskId 任务唯一标识
     * @param fileName 文件名
     * @param totalChunks 文档被分割的总片段数
     * @param processedChunks 已处理的片段数
     * @return 处理中状态的DTO对象，包含进度百分比
     */
    public static RagUploadStatusDTO processing(String taskId, String fileName, int totalChunks, int processedChunks) {
        int progress = totalChunks > 0 ? (int) ((processedChunks * 100.0) / totalChunks) : 0;
        return RagUploadStatusDTO.builder()
                .taskId(taskId)
                .fileName(fileName)
                .status(Status.PROCESSING)
                .progress(progress)
                .message(String.format("正在处理文档片段: %d/%d", processedChunks, totalChunks))
                .totalChunks(totalChunks)
                .processedChunks(processedChunks)
                .build();
    }

    /**
     * 创建已完成状态的DTO
     * @param taskId 任务唯一标识
     * @param fileName 文件名
     * @param totalChunks 处理的总片段数
     * @return 已完成状态的DTO对象，进度为100%
     */
    public static RagUploadStatusDTO completed(String taskId, String fileName, int totalChunks) {
        return RagUploadStatusDTO.builder()
                .taskId(taskId)
                .fileName(fileName)
                .status(Status.COMPLETED)
                .progress(100)
                .message("文档处理完成")
                .totalChunks(totalChunks)
                .processedChunks(totalChunks)
                .completedAt(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建失败状态的DTO
     * @param taskId 任务唯一标识
     * @param fileName 文件名
     * @param errorMessage 错误信息描述
     * @return 失败状态的DTO对象，包含错误详情
     */
    public static RagUploadStatusDTO failed(String taskId, String fileName, String errorMessage) {
        return RagUploadStatusDTO.builder()
                .taskId(taskId)
                .fileName(fileName)
                .status(Status.FAILED)
                .progress(0)
                .message("处理失败: " + errorMessage)
                .errorMessage(errorMessage)
                .build();
    }
}
