package com.itfelix.liuhaizhuaichat.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itfelix.liuhaizhuaichat.annotation.DistributedLock;
import com.itfelix.liuhaizhuaichat.pojo.dto.RagDocumentDTO;
import com.itfelix.liuhaizhuaichat.pojo.dto.RagUploadStatusDTO;
import com.itfelix.liuhaizhuaichat.service.RAGService;
import com.itfelix.liuhaizhuaichat.utils.RecursiveTextSplitter;
import com.itfelix.liuhaizhuaichat.records.OssUploadResult;
import com.itfelix.liuhaizhuaichat.utils.DistributedLockUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.recorder.FileRecorder;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * RAG（检索增强生成）服务实现类
 * 核心功能：
 * 1. 文档上传：异步处理，支持进度追踪
 * 2. 文档检索：基于向量相似度搜索
 * 3. 文档管理：列表查询、删除、重命名
 * 技术栈：
 * - Spring AI：文档解析、向量化
 * - Redis VectorStore：向量存储与检索
 * - OSS：原始文件存储
 * 
 * @author aceFelix
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RAGServiceImpl implements RAGService {
    
    /** 向量存储服务，用于文档的向量化存储和相似度搜索 */
    private final RedisVectorStore redisVectorStore;
    /** Redis操作模板，用于存储文档元数据和上传状态 */
    private final StringRedisTemplate stringRedisTemplate;
    /** 文件存储服务，用于上传和下载OSS文件 */
    private final FileStorageService fileStorageService;
    /** 文件记录器，用于查询和管理文件存储记录 */
    private final FileRecorder fileRecorder;
    /** 分布式锁工具类，用于防止重复操作 */
    private final DistributedLockUtil distributedLockUtil;
    /** JSON序列化工具，用于状态对象的序列化和反序列化 */
    private final ObjectMapper objectMapper;

    /** Redis中存储文档元数据的key前缀 */
    private static final String RAG_DOC_META_PREFIX = "rag:doc:meta:";
    /** Redis中存储上传状态的key前缀 */
    private static final String RAG_UPLOAD_STATUS_PREFIX = "rag:upload:status:";
    /** OSS存储路径前缀 */
    private static final String OSS_PATH_PREFIX = "knowledge-base/";
    /** 上传状态过期时间（小时） */
    private static final long STATUS_EXPIRE_HOURS = 24;

    /**
     * 异步上传RAG文档
     * 处理流程：
     * 1. 生成任务ID，读取文件内容
     * 2. 上传原始文件到OSS（同步，快速返回）
     * 3. 保存初始状态到Redis
     * 4. 启动异步处理线程（解析、分片、向量化）
     * 5. 立即返回任务状态，前端轮询进度
     * 
     * @param userId 用户ID
     * @param resource 文件资源
     * @param fileName 文件名
     * @return 上传状态DTO，包含任务ID
     */
    @Override
    @DistributedLock(key = "'rag:upload:' + #userId + ':' + #fileName", leaseTime = 60, message = "该文件正在上传中，请勿重复提交")
    public RagUploadStatusDTO uploadRagDocumentAsync(String userId, Resource resource, String fileName) {
        log.info("开始异步上传知识库文档: userId={}, fileName={}", userId, fileName);
        long startTime = System.currentTimeMillis();
        
        String taskId = UUID.randomUUID().toString();
        long fileSize = 0;
        
        try {
            fileSize = resource.contentLength();
        } catch (Exception e) {
            log.warn("无法获取文件大小: {}", fileName);
        }
        
        byte[] fileContent;
        try {
            fileContent = resource.getContentAsByteArray();
        } catch (IOException e) {
            log.error("读取文件失败: userId={}, fileName={}", userId, fileName, e);
            throw new RuntimeException("读取文件失败", e);
        }

        OssUploadResult uploadResult = uploadToOss(userId, fileName, fileContent);
        if (uploadResult == null) {
            throw new RuntimeException("上传文件到OSS失败");
        }

        RagUploadStatusDTO pendingStatus = RagUploadStatusDTO.pending(taskId, fileName, fileSize);
        saveUploadStatus(taskId, pendingStatus);

        processDocumentAsync(userId, fileName, fileContent, taskId, uploadResult.path(), uploadResult.url(), fileSize);

        long endTime = System.currentTimeMillis();
        log.info("文件上传到OSS完成，异步处理已启动: userId={}, fileName={}, taskId={}, 耗时: {}ms", 
                userId, fileName, taskId, (endTime - startTime));
        
        return pendingStatus;
    }

    /**
     * 异步处理文档（解析、分片、向量化）
     * 处理阶段：
     * 1. 解析文档（10%）：根据文件类型选择解析器
     * 2. 拆分文档（20%）：将长文档拆分成小片段
     * 3. 向量化处理（20%-90%）：批量添加到向量库
     * 4. 保存元数据（95%）：记录文档信息到Redis
     * 5. 完成（100%）：更新状态为完成
     * @Async注解使此方法在独立线程池中执行，不阻塞主线程
     */
    @Async("taskExecutor")
    public void processDocumentAsync(String userId, String fileName, byte[] fileContent, 
                                     String taskId, String ossPath, String fileUrl, long fileSize) {
        log.info("开始异步处理文档: userId={}, fileName={}, taskId={}", userId, fileName, taskId);
        
        try {
            updateUploadStatus(taskId, status -> {
                status.setStatus(RagUploadStatusDTO.Status.PROCESSING);
                status.setMessage("正在解析文档...");
                status.setProgress(10);
                return status;
            });

            List<Document> documents;
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            
            Resource byteArrayResource = new ByteArrayResource(fileContent);
            
            if (fileExtension.equals("txt")) {
                TextReader textReader = new TextReader(byteArrayResource);
                textReader.getCustomMetadata().put("fileName", fileName);
                documents = textReader.get();
                log.info("使用 TextReader 解析文本文件: {}", fileName);
            } else {
                TikaDocumentReader tikaReader = new TikaDocumentReader(byteArrayResource);
                documents = tikaReader.get();
                log.info("使用 TikaDocumentReader 解析文档: {}, 解析后文档数: {}", fileName, documents.size());
            }

            updateUploadStatus(taskId, status -> {
                status.setMessage("正在拆分文档...");
                status.setProgress(20);
                return status;
            });

            RecursiveTextSplitter textSplitter = new RecursiveTextSplitter(500, 50);
            List<Document> documentList = textSplitter.apply(documents);
            
            log.info("文档拆分完成: 原始文档数={}, 拆分后片段数={}", documents.size(), documentList.size());

            List<String> docIds = new ArrayList<>();
            List<Document> documentsWithIds = new ArrayList<>();
            
            for (Document doc : documentList) {
                String docId = UUID.randomUUID().toString();
                docIds.add(docId);
                
                Map<String, Object> metadata = new HashMap<>(doc.getMetadata());
                metadata.put("fileName", fileName);
                metadata.put("userId", userId);
                metadata.put("uploadTime", System.currentTimeMillis());
                
                Document newDoc = new Document(docId, doc.getText(), metadata);
                documentsWithIds.add(newDoc);
            }

            int totalChunks = documentsWithIds.size();
            int batchSize = 10;
            int processedBatches = 0;
            
            for (int i = 0; i < documentsWithIds.size(); i += batchSize) {
                List<Document> batch = documentsWithIds.subList(i, Math.min(i + batchSize, documentsWithIds.size()));
                redisVectorStore.add(batch);
                processedBatches++;
                
                int processedChunks = Math.min(i + batchSize, documentsWithIds.size());
                int progress = 20 + (int) ((processedChunks * 70.0) / totalChunks);
                
                updateUploadStatus(taskId, status -> {
                    status.setStatus(RagUploadStatusDTO.Status.PROCESSING);
                    status.setMessage(String.format("正在向量化处理: %d/%d 片段", processedChunks, totalChunks));
                    status.setProgress(progress);
                    status.setTotalChunks(totalChunks);
                    status.setProcessedChunks(processedChunks);
                    return status;
                });
                
                log.info("上传文档批次: {}/{}, 本批次 {} 个片段, 进度: {}%", 
                        processedBatches, (totalChunks + batchSize - 1) / batchSize, batch.size(), progress);
            }

            updateUploadStatus(taskId, status -> {
                status.setMessage("正在保存元数据...");
                status.setProgress(95);
                return status;
            });

            saveDocumentMeta(userId, fileName, fileSize, documentsWithIds.size(), docIds, ossPath, fileUrl);

            RagUploadStatusDTO completedStatus = RagUploadStatusDTO.completed(taskId, fileName, totalChunks);
            saveUploadStatus(taskId, completedStatus);

            log.info("用户 {} 成功处理文档: {}, 共 {} 个片段", userId, fileName, totalChunks);

        } catch (Exception e) {
            log.error("异步处理文档失败: userId={}, fileName={}, taskId={}, error={}", 
                    userId, fileName, taskId, e.getMessage(), e);
            
            RagUploadStatusDTO failedStatus = RagUploadStatusDTO.failed(taskId, fileName, e.getMessage());
            saveUploadStatus(taskId, failedStatus);
        }
    }

    /**
     * 查询文档上传处理状态
     * @param taskId 任务ID
     * @return 上传状态DTO
     */
    @Override
    public RagUploadStatusDTO getUploadStatus(String taskId) {
        String key = RAG_UPLOAD_STATUS_PREFIX + taskId;
        String json = stringRedisTemplate.opsForValue().get(key);
        
        if (json == null) {
            return RagUploadStatusDTO.builder()
                    .taskId(taskId)
                    .status(RagUploadStatusDTO.Status.FAILED)
                    .message("任务不存在或已过期")
                    .build();
        }
        
        try {
            return objectMapper.readValue(json, RagUploadStatusDTO.class);
        } catch (JsonProcessingException e) {
            log.error("解析上传状态失败: taskId={}", taskId, e);
            return RagUploadStatusDTO.builder()
                    .taskId(taskId)
                    .status(RagUploadStatusDTO.Status.FAILED)
                    .message("状态解析失败")
                    .build();
        }
    }

    /**
     * 保存上传状态到Redis
     * 状态24小时后自动过期
     */
    private void saveUploadStatus(String taskId, RagUploadStatusDTO status) {
        String key = RAG_UPLOAD_STATUS_PREFIX + taskId;
        try {
            String json = objectMapper.writeValueAsString(status);
            stringRedisTemplate.opsForValue().set(key, json, STATUS_EXPIRE_HOURS, TimeUnit.HOURS);
        } catch (JsonProcessingException e) {
            log.error("保存上传状态失败: taskId={}", taskId, e);
        }
    }

    /**
     * 更新上传状态
     * 使用函数式接口，允许调用方自定义更新逻辑
     */
    private void updateUploadStatus(String taskId, java.util.function.Function<RagUploadStatusDTO, RagUploadStatusDTO> updater) {
        RagUploadStatusDTO current = getUploadStatus(taskId);
        if (current != null && current.getStatus() != RagUploadStatusDTO.Status.FAILED) {
            RagUploadStatusDTO updated = updater.apply(current);
            saveUploadStatus(taskId, updated);
        }
    }

    /**
     * 从知识库中检索相关文档
     * 检索流程：
     * 1. 构建搜索请求，设置查询文本和返回数量
     * 2. 执行向量相似度搜索
     * 3. 过滤出当前用户的文档
     * 4. 返回相关文档列表
     * @param userId 用户ID
     * @param question 用户问题
     * @return 相关文档片段列表
     */
    @Override
    public List<Document> findRagDocument(String userId, String question) {
        log.info("开始向量搜索: userId={}, question={}", userId, question);
        
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(20)
                .similarityThreshold(0.0)
                .build();
        
        log.info("搜索参数: topK={}, threshold={}", 20, 0.0);
        
        List<Document> documents = redisVectorStore.similaritySearch(searchRequest);
        
        log.info("向量搜索结果数量: {}", documents.size());
        
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            String docUserId = (String) doc.getMetadata().get("userId");
            String docFileName = (String) doc.getMetadata().get("fileName");
            String text = doc.getText();
            log.info("结果[{}]: docUserId={}, fileName={}, text长度={}, text内容={}", 
                    i, docUserId, docFileName, text.length(), text);
        }
        
        List<Document> userDocuments = documents.stream()
                .filter(doc -> {
                    String docUserId = (String) doc.getMetadata().get("userId");
                    if (docUserId == null) {
                        log.warn("发现旧数据（metadata为null），暂时也返回");
                        return true;
                    }
                    boolean match = userId.equals(docUserId);
                    if (!match) {
                        log.debug("用户ID不匹配: 期望={}, 实际={}", userId, docUserId);
                    }
                    return match;
                })
                .collect(Collectors.toList());
        
        log.info("过滤后用户文档数量: {}", userDocuments.size());
        
        if (!userDocuments.isEmpty()) {
            log.info("用户 {} 知识库搜索: \"{}\", 找到 {} 个相关文档片段", userId, question, userDocuments.size());
            Set<String> fileNames = userDocuments.stream()
                    .map(doc -> (String) doc.getMetadata().get("fileName"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            log.info("相关文档来源: {}", fileNames);
        } else {
            log.info("用户 {} 知识库搜索: \"{}\", 未找到相关文档", userId, question);
        }
        
        return userDocuments;
    }

    /**
     * 获取用户的知识库文档列表
     * @param userId 用户ID
     * @return 文档DTO列表，按创建时间倒序排列
     */
    @Override
    public List<RagDocumentDTO> getRagDocumentList(String userId) {
        String userKeyPattern = RAG_DOC_META_PREFIX + userId + ":*";
        Set<String> keys = stringRedisTemplate.keys(userKeyPattern);
        if (keys == null || keys.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<RagDocumentDTO> documentList = new ArrayList<>();
        
        for (String key : keys) {
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
            if (entries != null && !entries.isEmpty()) {
                String fileName = (String) entries.get("fileName");
                String fileType = (String) entries.get("fileType");
                String sizeStr = (String) entries.get("size");
                String chunkCountStr = (String) entries.get("chunkCount");
                String createdAtStr = (String) entries.get("createdAt");
                
                RagDocumentDTO dto = RagDocumentDTO.builder()
                        .id(key.substring((RAG_DOC_META_PREFIX + userId + ":").length()))
                        .fileName(fileName)
                        .fileType(fileType != null ? fileType : getFileExtension(fileName))
                        .size(sizeStr != null ? Long.parseLong(sizeStr) : 0L)
                        .chunkCount(chunkCountStr != null ? Integer.parseInt(chunkCountStr) : 0)
                        .createdAt(createdAtStr != null ? LocalDateTime.parse(createdAtStr) : LocalDateTime.now())
                        .build();
                
                documentList.add(dto);
            }
        }
        
        documentList.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        
        return documentList;
    }

    /**
     * 删除知识库文档
     * 删除流程：
     * 1. 从Redis获取文档元数据（包含向量ID和OSS路径）
     * 2. 删除向量存储中的文档片段
     * 3. 删除OSS上的原始文件
     * 4. 删除Redis中的元数据
     * @param userId 用户ID
     * @param fileName 文件名
     */
    @Override
    @DistributedLock(key = "'rag:delete:' + #userId + ':' + #fileName", leaseTime = 30, message = "该文档正在删除中，请勿重复操作")
    public void deleteRagDocument(String userId, String fileName) {
        log.info("开始删除知识库文档: userId={}, fileName={}", userId, fileName);
        
        String metaKey = getUserMetaKey(userId, fileName);
        Map<Object, Object> metaData = stringRedisTemplate.opsForHash().entries(metaKey);
        
        String ossPath = null;
        String fileUrl = null;
        
        if (metaData != null && !metaData.isEmpty()) {
            String docIdsStr = (String) metaData.get("docIds");
            if (docIdsStr != null && !docIdsStr.isEmpty()) {
                List<String> docIdsToDelete = Arrays.asList(docIdsStr.split(","));
                
                if (!docIdsToDelete.isEmpty()) {
                    redisVectorStore.delete(docIdsToDelete);
                    log.info("成功从向量存储删除用户 {} 的文档: {}, 共 {} 个片段", userId, fileName, docIdsToDelete.size());
                }
            } else {
                log.warn("文档元数据中未找到docIds: {}", fileName);
            }
            
            ossPath = (String) metaData.get("ossPath");
            fileUrl = (String) metaData.get("fileUrl");
        } else {
            log.warn("未找到用户 {} 的文档元数据: {}", userId, fileName);
        }
        
        if (ossPath != null && !ossPath.isEmpty()) {
            deleteFromOss(ossPath, fileUrl);
        } else {
            log.warn("未找到OSS路径，无法删除OSS文件: userId={}, fileName={}", userId, fileName);
        }

        stringRedisTemplate.delete(metaKey);

        log.info("成功删除用户 {} 的文档: {}", userId, fileName);
    }

    /**
     * 重命名知识库文档
     * 只更新Redis中的元数据，不修改向量数据
     * 注意：向量中的fileName仍是旧名称，但不影响搜索功能
     * @param userId 用户ID
     * @param oldName 原文件名
     * @param newName 新文件名
     */
    @Override
    public void renameRagDocument(String userId, String oldName, String newName) {
        String oldMetaKey = getUserMetaKey(userId, oldName);
        Map<Object, Object> metaData = stringRedisTemplate.opsForHash().entries(oldMetaKey);
        
        if (metaData == null || metaData.isEmpty()) {
            log.warn("未找到文档元数据: userId={}, fileName={}", userId, oldName);
            return;
        }

        String newMetaKey = getUserMetaKey(userId, newName);
        
        metaData.put("fileName", newName);
        metaData.put("fileType", getFileExtension(newName));
        stringRedisTemplate.opsForHash().putAll(newMetaKey, metaData);
        stringRedisTemplate.delete(oldMetaKey);
        
        log.info("成功重命名用户 {} 的文档: {} -> {}", userId, oldName, newName);
    }

    /**
     * 批量删除知识库文档
     * @param userId 用户ID
     * @param fileNames 文件名列表
     */
    @Override
    @DistributedLock(key = "'rag:batchDelete:' + #userId", leaseTime = 60, message = "批量删除操作正在进行中，请勿重复操作")
    public void batchDeleteRagDocuments(String userId, List<String> fileNames) {
        log.info("开始批量删除知识库文档: userId={}, 文件数量={}", userId, fileNames.size());
        int successCount = 0;
        int failCount = 0;
        
        for (String fileName : fileNames) {
            try {
                deleteRagDocumentInternal(userId, fileName);
                successCount++;
            } catch (Exception e) {
                log.error("批量删除文档失败: userId={}, fileName={}, error={}", userId, fileName, e.getMessage());
                failCount++;
            }
        }
        
        log.info("批量删除完成: userId={}, 成功={}, 失败={}", userId, successCount, failCount);
    }

    /**
     * 内部删除方法（不加锁）
     * 供批量删除调用，避免重复加锁
     */
    private void deleteRagDocumentInternal(String userId, String fileName) {
        String metaKey = getUserMetaKey(userId, fileName);
        Map<Object, Object> metaData = stringRedisTemplate.opsForHash().entries(metaKey);
        
        if (metaData != null && !metaData.isEmpty()) {
            String docIdsStr = (String) metaData.get("docIds");
            if (docIdsStr != null && !docIdsStr.isEmpty()) {
                List<String> docIdsToDelete = Arrays.asList(docIdsStr.split(","));
                if (!docIdsToDelete.isEmpty()) {
                    redisVectorStore.delete(docIdsToDelete);
                }
            }
            
            String ossPath = (String) metaData.get("ossPath");
            String fileUrl = (String) metaData.get("fileUrl");
            if (ossPath != null && !ossPath.isEmpty()) {
                deleteFromOss(ossPath, fileUrl);
            }
        }
        
        stringRedisTemplate.delete(metaKey);
    }
    
    /**
     * 保存文档元数据到Redis
     * 存储内容：
     * - fileName: 文件名
     * - fileType: 文件类型
     * - size: 文件大小
     * - chunkCount: 片段数量
     * - createdAt: 创建时间
     * - userId: 用户ID
     * - ossPath: OSS存储路径
     * - fileUrl: 文件访问URL
     * - docIds: 文档片段ID列表（逗号分隔）
     */
    private void saveDocumentMeta(String userId, String fileName, long fileSize, int chunkCount, List<String> docIds, String ossPath, String fileUrl) {
        String key = getUserMetaKey(userId, fileName);

        Map<String, String> metaData = new HashMap<>();
        metaData.put("fileName", fileName);
        metaData.put("fileType", getFileExtension(fileName));
        metaData.put("size", String.valueOf(fileSize));
        metaData.put("chunkCount", String.valueOf(chunkCount));
        metaData.put("createdAt", LocalDateTime.now().toString());
        metaData.put("userId", userId);
        metaData.put("ossPath", ossPath);
        metaData.put("fileUrl", fileUrl);

        if (docIds != null && !docIds.isEmpty()) {
            metaData.put("docIds", String.join(",", docIds));
        }

        stringRedisTemplate.opsForHash().putAll(key, metaData);
        log.info("保存用户 {} 的文档元数据: {}, 大小: {} bytes, 片段数: {}, 文档ID数: {}, OSS路径: {}, URL: {}",
                userId, fileName, fileSize, chunkCount, docIds != null ? docIds.size() : 0, ossPath, fileUrl);
    }
    
    /**
     * 获取用户文档元数据的Redis Key
     */
    private String getUserMetaKey(String userId, String fileName) {
        return RAG_DOC_META_PREFIX + userId + ":" + fileName;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "unknown";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 上传文件到OSS
     * @param userId 用户ID
     * @param fileName 文件名
     * @param content 文件内容
     * @return OSS上传结果（路径和URL）
     */
    private OssUploadResult uploadToOss(String userId, String fileName, byte[] content) {
        try {
            String ossPath = OSS_PATH_PREFIX + userId + "/";

            var uploadResult = fileStorageService.of(new ByteArrayInputStream(content))
                    .setPath(ossPath)
                    .setSaveFilename(fileName)
                    .upload();

            String fileUrl = uploadResult.getUrl();
            String fullOssPath = ossPath + fileName;
            log.info("文件上传到OSS成功: path={}, fullPath={}, url={}", ossPath, fullOssPath, fileUrl);
            return new OssUploadResult(fullOssPath, fileUrl);
        } catch (Exception e) {
            log.error("上传文件到OSS失败: userId={}, fileName={}", userId, fileName, e);
            return null;
        }
    }

    /**
     * 从OSS获取文档内容
     * @param userId 用户ID
     * @param fileName 文件名
     * @return 文档内容字节数组
     */
    @Override
    public byte[] getDocumentContent(String userId, String fileName) {
        try {
            String metaKey = getUserMetaKey(userId, fileName);
            Map<Object, Object> metaData = stringRedisTemplate.opsForHash().entries(metaKey);

            if (metaData == null || metaData.isEmpty()) {
                log.warn("未找到文档元数据: userId={}, fileName={}", userId, fileName);
                return null;
            }

            String ossPath = (String) metaData.get("ossPath");
            if (ossPath == null || ossPath.isEmpty()) {
                log.warn("文档元数据中未找到OSS路径: userId={}, fileName={}", userId, fileName);
                return null;
            }

            return fileStorageService.download(ossPath).bytes();
        } catch (Exception e) {
            log.error("从OSS获取文档失败: userId={}, fileName={}", userId, fileName, e);
            return null;
        }
    }

    /**
     * 从OSS删除文件
     * 删除策略：
     * 1. 优先使用FileInfo删除（同时删除OSS文件和数据库记录）
     * 2. 如果数据库无记录，使用路径直接删除
     * @param ossPath OSS存储路径
     * @param fileUrl 文件URL
     */
    private void deleteFromOss(String ossPath, String fileUrl) {
        try {
            if (fileUrl != null && !fileUrl.isEmpty()) {
                FileInfo fileInfo = fileRecorder.getByUrl(fileUrl);
                if (fileInfo != null) {
                    fileStorageService.delete(fileInfo);
                    log.info("从OSS删除文件成功: url={}, path={}", fileUrl, ossPath);
                } else {
                    log.warn("数据库中未找到文件记录，尝试直接删除: url={}, path={}", fileUrl, ossPath);
                    FileInfo deleteInfo = new FileInfo();
                    deleteInfo.setUrl(fileUrl);
                    deleteInfo.setPath(ossPath);
                    deleteInfo.setPlatform("aliyun-oss-1");
                    fileStorageService.delete(deleteInfo);
                    log.info("使用路径删除文件成功: {}", ossPath);
                }
            } else if (ossPath != null && !ossPath.isEmpty()) {
                log.warn("文件URL为空，尝试使用路径删除: {}", ossPath);
                FileInfo deleteInfo = new FileInfo();
                deleteInfo.setPath(ossPath);
                deleteInfo.setPlatform("aliyun-oss-1");
                fileStorageService.delete(deleteInfo);
                log.info("使用路径删除文件成功: {}", ossPath);
            } else {
                log.warn("OSS路径和URL都为空，无法删除文件");
            }
        } catch (Exception e) {
            log.error("从OSS删除文件失败: ossPath={}, fileUrl={}", ossPath, fileUrl, e);
        }
    }
}
