package com.itfelix.liuhaizhuaichat.controller.admin;

import com.itfelix.liuhaizhuaichat.annotation.RequirePermission;
import com.itfelix.liuhaizhuaichat.mapper.UserMapper;
import com.itfelix.liuhaizhuaichat.pojo.dto.RagDocumentDTO;
import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import com.itfelix.liuhaizhuaichat.service.RAGService;
import com.itfelix.liuhaizhuaichat.utils.AceResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * 提供管理员专属功能接口
 *
 * @author aceFelix
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRAGController {

    private final RAGService ragService;
    private final UserMapper userMapper;

    /**
     * 获取所有知识库文档列表（管理员）
     * @return 所有用户的知识库文档列表
     */
    @GetMapping("/knowledge/list")
    @RequirePermission(admin = true)
    public AceResult<List<RagDocumentDTO>> getAllKnowledgeDocs() {
        log.info("管理员获取所有知识库文档列表");

        // 获取所有用户
        List<User> allUsers = userMapper.selectList(null);
        List<RagDocumentDTO> allDocs = new ArrayList<>();

        // 遍历所有用户，获取每个用户的知识库文档
        for (User user : allUsers) {
            try {
                List<RagDocumentDTO> userDocs = ragService.getRagDocumentList(user.getUserId());
                // 为每个文档添加用户信息
                for (RagDocumentDTO doc : userDocs) {
                    doc.setUserId(user.getUserId());
                    doc.setUsername(user.getUsername());
                    doc.setUserAvatar(user.getAvatar());
                }
                allDocs.addAll(userDocs);
            } catch (Exception e) {
                log.warn("获取用户 {} 的知识库文档失败: {}", user.getUserId(), e.getMessage());
            }
        }

        // 按上传时间倒序排序
        allDocs.sort((a, b) -> {
            if (a.getUploadTime() == null || b.getUploadTime() == null) {
                return 0;
            }
            return b.getUploadTime().compareTo(a.getUploadTime());
        });

        return AceResult.success(allDocs);
    }

    /**
     * 批量删除知识库文档
     * @param request 包含文档列表的请求
     * @return 删除结果
     */
    @PostMapping("/knowledge/batch-delete")
    @RequirePermission(admin = true)
    public AceResult<Void> batchDeleteKnowledgeDocs(@RequestBody Map<String, List<Map<String, String>>> request) {
        List<Map<String, String>> docs = request.get("docs");
        if (docs == null || docs.isEmpty()) {
            return AceResult.error("没有要删除的文档");
        }

        log.info("管理员批量删除知识库文档，数量: {}", docs.size());

        int successCount = 0;
        int failCount = 0;

        for (Map<String, String> doc : docs) {
            String userId = doc.get("userId");
            String fileName = doc.get("fileName");

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
     * 获取知识库统计信息
     * @return 统计信息
     */
    @GetMapping("/knowledge/stats")
    @RequirePermission(admin = true)
    public AceResult<Map<String, Object>> getKnowledgeStats() {
        log.info("管理员获取知识库统计信息");

        // 获取所有用户
        List<User> allUsers = userMapper.selectList(null);
        int totalDocs = 0;
        long totalStorage = 0;

        // 遍历所有用户统计
        for (User user : allUsers) {
            try {
                List<RagDocumentDTO> userDocs = ragService.getRagDocumentList(user.getUserId());
                totalDocs += userDocs.size();
                for (RagDocumentDTO doc : userDocs) {
                    if (doc.getFileSize() != null) {
                        totalStorage += doc.getFileSize();
                    }
                }
            } catch (Exception e) {
                log.warn("统计用户 {} 的知识库失败: {}", user.getUserId(), e.getMessage());
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDocs", totalDocs);
        stats.put("totalStorage", totalStorage);
        stats.put("userCount", allUsers.size());

        return AceResult.success(stats);
    }

    /**
     * 获取系统概览统计
     * @return 系统统计数据
     */
    @GetMapping("/stats")
    @RequirePermission(admin = true)
    public AceResult<Map<String, Object>> getSystemStats() {
        log.info("管理员获取系统统计信息");

        // 获取所有用户
        List<User> allUsers = userMapper.selectList(null);

        int totalUsers = allUsers.size();
        int vipUsers = 0;
        int adminUsers = 0;
        int activeUsers = 0;

        for (User user : allUsers) {
            switch (user.getRole()) {
                case VIP:
                    vipUsers++;
                    break;
                case ADMIN:
                    adminUsers++;
                    break;
                default:
                    break;
            }
            if (user.getStatus() == 1) {
                activeUsers++;
            }
        }

        // 知识库统计
        int totalDocs = 0;
        long totalStorage = 0;
        for (User user : allUsers) {
            try {
                List<RagDocumentDTO> userDocs = ragService.getRagDocumentList(user.getUserId());
                totalDocs += userDocs.size();
                for (RagDocumentDTO doc : userDocs) {
                    if (doc.getFileSize() != null) {
                        totalStorage += doc.getFileSize();
                    }
                }
            } catch (Exception e) {
                // 忽略错误
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("vipUsers", vipUsers);
        stats.put("adminUsers", adminUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("totalDocs", totalDocs);
        stats.put("totalStorage", totalStorage);

        return AceResult.success(stats);
    }

    /**
     * 下载知识库文档
     * @param userId   用户ID
     * @param fileName 文件名
     * @param response HTTP响应
     */
    @GetMapping("/knowledge/download")
    @RequirePermission(admin = true)
    public void downloadKnowledgeDoc(@RequestParam("userId") String userId,
                                      @RequestParam("fileName") String fileName,
                                      HttpServletResponse response) {
        log.info("管理员下载知识库文档: userId={}, fileName={}", userId, fileName);

        try {
            // 获取文档内容
            byte[] content = ragService.getDocumentContent(userId, fileName);

            if (content == null || content.length == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setContentLength(content.length);

            // 写入响应
            response.getOutputStream().write(content);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("下载文档失败: userId={}, fileName={}, error={}", userId, fileName, e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 预览知识库文档
     * @param userId   用户ID
     * @param fileName 文件名
     * @return 文档内容
     */
    @GetMapping("/knowledge/preview")
    @RequirePermission(admin = true)
    public AceResult<Map<String, Object>> previewKnowledgeDoc(@RequestParam("userId") String userId,
                                                               @RequestParam("fileName") String fileName) {
        log.info("管理员预览知识库文档: userId={}, fileName={}", userId, fileName);

        try {
            // 获取文档内容
            byte[] content = ragService.getDocumentContent(userId, fileName);

            if (content == null || content.length == 0) {
                return AceResult.error("文档不存在或为空");
            }

            // 转换为文本（限制长度）
            String textContent = new String(content, "UTF-8");
            if (textContent.length() > 5000) {
                textContent = textContent.substring(0, 5000) + "...";
            }

            Map<String, Object> result = new HashMap<>();
            result.put("content", textContent);
            result.put("fileName", fileName);
            result.put("size", content.length);

            return AceResult.success(result);
        } catch (Exception e) {
            log.error("预览文档失败: userId={}, fileName={}, error={}", userId, fileName, e.getMessage());
            return AceResult.error("预览文档失败: " + e.getMessage());
        }
    }
}
