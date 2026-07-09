package com.itfelix.liuhaizhuaichat.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * Query 改写工具
 * 使用 DashScope qwen-flash 模型将用户口语化问题改写为检索友好的关键词形式
 * 提升 RAG 向量检索的召回率
 *
 * @author aceFelix
 */
@Slf4j
@Component
public class QueryRewriteUtil {

    private static final String REWRITE_PROMPT = """
            你是一个搜索查询优化器。将用户的口语化问题改写为适合向量检索的关键词组合。
            规则：
            1. 提取核心实体、技术名词、关键概念
            2. 去除语气词和废话，保留信息密度
            3. 如果问题本身已是精炼查询，保持不变
            4. 只输出改写后的查询文本，不要任何解释

            【用户问题】
            %s
            """;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private final String apiKey;
    private final String apiUrl;
    private final String model;

    public QueryRewriteUtil(OkHttpClient httpClient,
                            ObjectMapper objectMapper,
                            @Value("${DASHSCOPE_API_KEY:}") String apiKey,
                            @Value("${dashscope.rewrite.url:https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation}") String apiUrl,
                            @Value("${dashscope.rewrite.model:qwen-flash}") String model) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
    }

    /**
     * 改写用户查询，提升向量检索召回率
     *
     * @param originalQuery 用户原始问题
     * @return 改写后的查询文本；失败时返回原始问题
     */
    public String rewrite(String originalQuery) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("DASHSCOPE_API_KEY 未配置，跳过 Query 改写");
            return originalQuery;
        }
        if (originalQuery == null || originalQuery.isBlank()) {
            return originalQuery;
        }

        try {
            String prompt = String.format(REWRITE_PROMPT, originalQuery);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            Map<String, Object> input = new HashMap<>();
            List<Map<String, String>> messages = new ArrayList<>();

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);

            input.put("messages", messages);
            requestBody.put("input", input);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("max_tokens", 200);
            parameters.put("temperature", 0.1);
            parameters.put("result_format", "message");
            requestBody.put("parameters", parameters);

            String json = objectMapper.writeValueAsString(requestBody);

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(json, MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("Query 改写 API 调用失败: HTTP {}, 回退到原始 query", response.code());
                    return originalQuery;
                }

                String responseBody = response.body().string();
                JsonNode root = objectMapper.readTree(responseBody);
                JsonNode output = root.get("output");

                if (output == null) {
                    log.warn("Query 改写响应无 output 字段，回退到原始 query");
                    return originalQuery;
                }

                JsonNode choices = output.get("choices");
                if (choices != null && choices.isArray() && choices.size() > 0) {
                    JsonNode message = choices.get(0).get("message");
                    if (message != null) {
                        String rewritten = message.get("content").asText();
                        if (rewritten != null && !rewritten.isBlank()) {
                            log.debug("Query 改写成功: \"{}\" → \"{}\"", originalQuery, rewritten);
                            return rewritten.trim();
                        }
                    }
                }

                log.warn("Query 改写响应解析失败，回退到原始 query");
                return originalQuery;
            }
        } catch (IOException e) {
            log.warn("Query 改写异常: {}，回退到原始 query", e.getMessage());
            return originalQuery;
        } catch (Exception e) {
            log.warn("Query 改写未知异常: {}，回退到原始 query", e.getMessage());
            return originalQuery;
        }
    }
}
