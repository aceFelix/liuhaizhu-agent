package com.itfelix.liuhaizhuaichat.controller;

import com.itfelix.liuhaizhuaichat.pojo.dto.ChatDTO;
import com.itfelix.liuhaizhuaichat.service.ChatService;
import com.itfelix.liuhaizhuaichat.utils.AceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * 文件上传聊天Controller
 * 支持上传文件后，基于文件内容进行问答
 *
 * @author aceFelix
 */
@Slf4j
@RestController
@RequestMapping("/api/fileChat")
public class FileChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 上传文件并进行聊天
     * 解析文件内容，将内容作为上下文发送给AI
     *
     * @param file 上传的文件
     * @param message 用户消息
     * @param userMessageId 用户消息ID
     * @param botMessageId 机器人消息ID
     * @param conversationId 会话ID（可选）
     * @return 操作结果
     */
    @PostMapping("/chat")
    public AceResult<Void> chatWithFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("message") String message,
            @RequestParam("userMessageId") String userMessageId,
            @RequestParam("botMessageId") String botMessageId,
            @RequestParam("userId") String userId,
            @RequestParam(value = "conversationId", required = false) String conversationId) {

        String fileName = file.getOriginalFilename();

        log.info("用户上传文件进行聊天: userId={}, fileName={}, message={}", userId, fileName, message);

        // 1. 解析文件内容
        String fileContent;
        try {
            fileContent = parseFileContent(file, fileName);
            log.info("文件解析成功: fileName={}, contentLength={}", fileName, fileContent.length());
        } catch (Exception e) {
            log.error("文件解析失败: fileName={}", fileName, e);
            return AceResult.error("文件解析失败: " + e.getMessage());
        }

        // 2. 检查文件内容长度，避免超出模型上下文限制
        if (fileContent.length() > 50000) {
            log.warn("文件内容过长，进行截断: fileName={}, originalLength={}", fileName, fileContent.length());
            fileContent = fileContent.substring(0, 50000) + "\n...[内容已截断]";
        }

        // 3. 构建ChatEntity
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setUserId(userId);
        chatDTO.setMessage(message);
        chatDTO.setUserMessageId(userMessageId);
        chatDTO.setBotMessageId(botMessageId);
        chatDTO.setConversationId(conversationId);
        chatDTO.setFileContent(fileContent);
        chatDTO.setFileName(fileName);

        // 4. 调用Service进行聊天
        chatService.doChatWithFile(chatDTO);

        return AceResult.success();
    }

    /**
     * 解析文件内容
     *
     * @param file 上传的文件
     * @param fileName 文件名
     * @return 文件内容
     * @throws IOException 读取文件异常
     */
    private String parseFileContent(MultipartFile file, String fileName) throws IOException {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        // 根据文件类型选择解析方式
        if (fileExtension.equals("txt")) {
            // 纯文本文件直接读取
            TextReader textReader = new TextReader(new InputStreamResource(file.getInputStream()));
            return textReader.get().stream()
                    .map(doc -> doc.getText())
                    .collect(Collectors.joining("\n"));
        } else {
            // 其他格式使用Tika解析（支持PDF、Word、PPT等）
            TikaDocumentReader tikaReader = new TikaDocumentReader(new InputStreamResource(file.getInputStream()));
            return tikaReader.get().stream()
                    .map(doc -> doc.getText())
                    .collect(Collectors.joining("\n"));
        }
    }
}
