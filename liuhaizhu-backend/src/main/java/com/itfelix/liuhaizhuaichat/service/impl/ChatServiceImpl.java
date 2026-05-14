package com.itfelix.liuhaizhuaichat.service.impl;

import cn.hutool.json.JSONUtil;
import com.itfelix.liuhaizhuaichat.annotation.RateLimit;
import com.itfelix.liuhaizhuaichat.pojo.dto.ChatDTO;
import com.itfelix.liuhaizhuaichat.pojo.dto.ChatResponseDTO;
import com.itfelix.liuhaizhuaichat.pojo.dto.WebSearchResult;
import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import com.itfelix.liuhaizhuaichat.enums.SSEMessageType;
import com.itfelix.liuhaizhuaichat.service.ChatService;
import com.itfelix.liuhaizhuaichat.service.SearXngService;
import com.itfelix.liuhaizhuaichat.service.ConversationService;
import com.itfelix.liuhaizhuaichat.mapper.UserMapper;
import com.itfelix.liuhaizhuaichat.utils.SSEServerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author aceFelix
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    @Qualifier("qwen3-max")
    private ChatClient qwen3MaxChatClient;

    @Autowired
    private SearXngService searXngService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    @Lazy
    private ChatServiceImpl self;

    /**
     * 与大模型交互
     * 添加限流：每秒最多5个请求，桶容量10
     * @param chatDTO
     */
    @Override
    @RateLimit(key = "'chat:' + #chatDTO.userId", capacity = 10, rate = 5, message = "聊天请求过于频繁，请稍后重试")
    public void doChat(ChatDTO chatDTO) {
        String userId = chatDTO.getUserId();
        String prompt = chatDTO.getMessage();
        String botMessageId = chatDTO.getBotMessageId();
        String conversationId = chatDTO.getConversationId();
        String userMessageId = chatDTO.getUserMessageId();

        // 保存用户消息到数据库（仅当conversationId不为null时保存）
        if (conversationId != null && userMessageId != null) {
            conversationService.saveUserMessage(conversationId, userId, prompt, userMessageId);
        }

        // 与大模型交互,并实时推送消息给前端
        self.pushMessage(userId, prompt, botMessageId, conversationId);
    }

    // RAG搜索提示词
    private static final String RAG_PROMPT = """
            你是刘海柱，一个基于知识库的AI助手。用户已经上传了知识库文档，以下是相关知识库内容。
            
            【知识库内容 - 必须优先使用这些信息回答】
            {context}
            
            【用户问题】
            {question}
            
            【回答规则 - 必须严格遵守】
            1. 优先使用知识库内容回答用户问题
            2. 如果知识库中有相关信息，必须基于知识库内容回答，不要说你不知道
            3. 如果知识库内容不够完整，可以结合你的知识进行补充，但要明确区分知识库内容和你补充的内容
            4. 保持刘海柱的说话风格（带东北口音、直爽、有时带点幽默）
            5. 回答要实用、具体，给出明确的步骤或建议
            6. 如果知识库中没有一点相关的内容，直接回复："我操了，你这知识库里根本查不到啊！还是我直接用我的知识来告诉你吧。"然后进行你的回复。
            """;
    /**
     * 与大模型交互（启用RAG搜索功能）
     * 添加限流：每秒最多3个请求，桶容量5（RAG查询更消耗资源）
     * @param chatDTO
     * @param ragContext
     */
    @Override
    @RateLimit(key = "'rag_chat:' + #chatDTO.userId", capacity = 5, rate = 3, message = "RAG聊天请求过于频繁，请稍后重试")
    public void doChatRagSearch(ChatDTO chatDTO, List<Document> ragContext) {
        String userId = chatDTO.getUserId();
        String question = chatDTO.getMessage();
        String botMessageId = chatDTO.getBotMessageId();
        String conversationId = chatDTO.getConversationId();
        String userMessageId = chatDTO.getUserMessageId();
        
        // 保存用户消息到数据库
        if (conversationId != null && userMessageId != null) {
            conversationService.saveUserMessage(conversationId, userId, question, userMessageId);
        }

        // 构建提示词
        String context = ragContext.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));
        
        // 组装提示词
        String prompt = RAG_PROMPT
                .replace("{context}", context)
                .replace("{question}", question);
        
        self.pushMessage(userId, prompt, botMessageId, conversationId);
    }


    // 联网搜索提示词
    private static final String SEARCH_PROMPT = """
            你是刘海柱，一个擅长联网搜索的AI助手。你的任务是基于搜索结果回答用户问题。
            
            【搜索结果】
            {context}
            
            【用户问题】
            {question}
            
            【处理搜索结果的步骤】
            1. 仔细阅读所有搜索结果，判断哪些结果与用户问题相关
            2. 从相关结果中提取关键信息，进行整理和归纳
            3. 如果多个结果提供相同或相似信息，综合这些信息
            4. 如果结果之间存在矛盾，指出矛盾点并说明可能的原因
            5. 将整理好的信息用刘海柱的口吻回答用户
            
            【回答要求】
            1. 必须基于搜索结果回答，不能凭空编造
            2. 如果搜索结果中有明确的答案，直接给出答案并说明来源
            3. 如果搜索结果中没有直接答案，但有关联信息，基于关联信息进行分析，并说明这是基于搜索结果的推断
            4. 如果搜索结果完全不相关或质量太差，先说："我去你的，这破网搜出来的都是啥玩意儿，没找到靠谱的信息！"然后基于你自己的知识回答，但要明确说明这是你自己的知识，不是搜索结果
            5. 保持刘海柱的说话风格（东北口音、直爽、幽默）
            6. 回答要具体、实用，给出明确的答案或建议
            7. 如果引用搜索结果，最好说明来源（比如"根据XX网站的信息..."）
            8. 不要把搜索结果的原文直接复制粘贴，要用自己的话重新组织
            
            【重要提醒】
            - 搜索结果可能包含错误或过时的信息，要仔细甄别
            - 如果搜索结果看起来像是广告或垃圾内容，直接忽略
            - 如果搜索结果数量为0或质量很差，不要强行使用搜索结果
            """;

    // 文件上传聊天提示词
    private static final String FILE_CHAT_PROMPT = """
            你是刘海柱，一个擅长分析文档的AI助手。
            用户上传了一个文件，你需要基于文件内容回答用户的问题。
            
            【文件信息】
            文件名：{fileName}
            
            【文件内容】
            {fileContent}
            
            【用户问题】
            {question}
            
            【回答规则】
            1. 仔细阅读文件内容，基于文件内容回答用户问题
            2. 如果文件内容中有明确答案，直接回答
            3. 如果文件内容中没有直接答案，但有关联信息，可以基于关联信息进行分析
            4. 如果文件内容完全不相关，直接告诉用户"文件中没有相关内容"
            5. 保持刘海柱的说话风格（带东北口音、直爽、有时带点幽默）
            6. 回答要具体、实用
            """;
    /**
     * 与大模型交互（启用联网搜索）
     * @param chatDTO
     */
    @Override
    public void doChatWebSearch(ChatDTO chatDTO) {
        String userId = chatDTO.getUserId();
        String question = chatDTO.getMessage();
        String botMessageId = chatDTO.getBotMessageId();
        String conversationId = chatDTO.getConversationId();
        String userMessageId = chatDTO.getUserMessageId();
        
        // 保存用户消息到数据库
        if (conversationId != null && userMessageId != null) {
            conversationService.saveUserMessage(conversationId, userId, question, userMessageId);
        }
        
        // 构建提示词
        List<WebSearchResult> search = searXngService.search(question);
        StringBuffer context = new StringBuffer();
        for (WebSearchResult webSearchResult : search) {
            // context.append(webSearchResult.getTitle()).append("\n").append(webSearchResult.getContent()).append("\n");
            context.append(
                    String.format("<context>\n【来源】%s \n【内容摘要】%s \n</context>\n",
                            webSearchResult.getUrl(),
                            webSearchResult.getContent()
                            )
            );
        }
        // 组装提示词
        String prompt = SEARCH_PROMPT
                .replace("{context}", context)
                .replace("{question}", question);
        
        self.pushMessage(userId, prompt, botMessageId, conversationId);
    }

    /**
     * 与大模型交互（基于上传的文件内容）
     * 添加限流：每秒最多3个请求，桶容量5
     * @param chatDTO
     */
    @Override
    @RateLimit(key = "'file_chat:' + #chatDTO.userId", capacity = 5, rate = 3, message = "文件聊天请求过于频繁，请稍后重试")
    public void doChatWithFile(ChatDTO chatDTO) {
        String userId = chatDTO.getUserId();
        String question = chatDTO.getMessage();
        String botMessageId = chatDTO.getBotMessageId();
        String conversationId = chatDTO.getConversationId();
        String userMessageId = chatDTO.getUserMessageId();
        String fileContent = chatDTO.getFileContent();
        String fileName = chatDTO.getFileName();

        // 保存用户消息到数据库
        if (conversationId != null && userMessageId != null) {
            conversationService.saveUserMessage(conversationId, userId, question, userMessageId);
        }

        // 构建提示词
        String prompt = FILE_CHAT_PROMPT
                .replace("{fileName}", fileName != null ? fileName : "未知文件")
                .replace("{fileContent}", fileContent != null ? fileContent : "")
                .replace("{question}", question);

        self.pushMessage(userId, prompt, botMessageId, conversationId);
    }


    /**
     * 与大模型交互方法，将响应内容实时推送给前端
     * 使用异步处理避免阻塞，添加完善的异常处理和资源清理
     * 
     * @param userId 用户ID
     * @param prompt 提示词
     * @param botMessageId AI消息ID
     * @param conversationId 会话ID
     */
    @Async
    public CompletableFuture<Void> pushMessage(String userId, String prompt, String botMessageId, String conversationId) {
        List<String> contentList = new ArrayList<>();
        
        try {
            log.info("开始处理AI对话, userId: {}, conversationId: {}", userId, conversationId);
            
            // 获取用户信息
            User user = userMapper.selectById(userId);
            String userEmail = user != null ? user.getEmail() : null;
            String userName = user != null ? user.getUsername() : null;

            // 在提示词中加入用户信息，让AI知道当前用户的信息
            String enhancedPrompt = buildPromptWithUserInfo(prompt, userId, userName, userEmail);

            // 调用大模型流式输出，设置超时时间120秒
            Flux<String> result = qwen3MaxChatClient.prompt(enhancedPrompt)
                    .stream()
                    .content()
                    .timeout(Duration.ofSeconds(120));

            // 实时推送每个Token
            result.toStream()
                    .map(chatResponse -> {
                        String content = chatResponse.toString();
                        
                        // 推送增量消息
                        SSEServerUtil.sendMessage(userId, content, SSEMessageType.ADD);
                        log.debug("推送Token: {}", content);
                        
                        return content;
                    })
                    .forEach(contentList::add);

            // 拼接完整的响应内容
            String fullContent = String.join("", contentList);
            int tokenCount = estimateTokenCount(fullContent);
            log.info("AI对话完成, userId: {}, 响应长度: {}, 估算Token: {}", userId, fullContent.length(), tokenCount);

            if (conversationId != null && botMessageId != null) {
                try {
                    conversationService.saveAssistantMessage(conversationId, userId, fullContent, botMessageId, tokenCount);
                    log.info("AI回复已保存到数据库, conversationId: {}, messageId: {}", conversationId, botMessageId);
                } catch (Exception e) {
                    log.error("保存AI回复失败, conversationId: {}, messageId: {}", conversationId, botMessageId, e);
                }
            }

            // 发送完成信号
            ChatResponseDTO chatResponseDTO = new ChatResponseDTO(fullContent, botMessageId);
            SSEServerUtil.sendMessage(userId, JSONUtil.toJsonStr(chatResponseDTO), SSEMessageType.FINISH);
            
            return CompletableFuture.completedFuture(null);
            
        } catch (Exception e) {
            log.error("AI对话处理失败, userId: {}, conversationId: {}", userId, conversationId, e);
            
            // 发送错误消息给前端
            String errorMessage = "AI服务暂时不可用，请稍后重试";
            SSEServerUtil.sendMessage(userId, errorMessage, SSEMessageType.ERROR);
            
            // 发送失败响应
            ChatResponseDTO errorResponse = new ChatResponseDTO(errorMessage, botMessageId);
            SSEServerUtil.sendMessage(userId, JSONUtil.toJsonStr(errorResponse), SSEMessageType.FINISH);
            
            // 清理SSE连接
            SseEmitter emitter = SSEServerUtil.getSseClients().get(userId);
            if (emitter != null) {
                SSEServerUtil.remove(userId, emitter);
            }
            
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 构建带用户信息的提示词
     * 在提示词中加入当前用户信息，供AI使用
     */
    private String buildPromptWithUserInfo(String originalPrompt, String userId, String username, String email) {
        StringBuilder sb = new StringBuilder();
        sb.append("【当前用户信息 - AI必须记住】\n");
        sb.append("用户ID: ").append(userId).append("\n");
        if (username != null) {
            sb.append("用户名: ").append(username).append("\n");
        }
        if (email != null) {
            sb.append("用户邮箱: ").append(email).append("\n");
        }
        sb.append("\n重要提示：以上用户信息已经提供给你，当用户询问自己的邮箱时，直接回答即可，不需要调用任何工具查询。\n\n");
        sb.append("【用户消息】\n");
        sb.append(originalPrompt);
        return sb.toString();
    }

    private int estimateTokenCount(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        int chineseChars = 0;
        int otherChars = 0;
        for (char c : text.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN) {
                chineseChars++;
            } else if (!Character.isWhitespace(c)) {
                otherChars++;
            }
        }
        return chineseChars + (int) Math.ceil(otherChars / 3.5);
    }
}