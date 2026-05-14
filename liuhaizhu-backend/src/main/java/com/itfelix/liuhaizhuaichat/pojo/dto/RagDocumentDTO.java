package com.itfelix.liuhaizhuaichat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 知识库文档DTO
 * @author aceFelix
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagDocumentDTO {
    
    /**
     * 文档ID
     */
    private String id;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 文件大小（字节）
     */
    private Long size;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 文档片段数量
     */
    private Integer chunkCount;

    /**
     * 用户ID（用于管理员查看）
     */
    private String userId;

    /**
     * 用户名（用于管理员查看）
     */
    private String username;

    /**
     * 用户头像（用于管理员查看）
     */
    private String userAvatar;

    /**
     * 文件大小（别名，兼容前端）
     */
    public Long getFileSize() {
        return this.size;
    }

    /**
     * 上传时间（别名，兼容前端）
     */
    public LocalDateTime getUploadTime() {
        return this.createdAt;
    }
}
