package com.itfelix.liuhaizhuaichat.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Cross-Encoder 重排序工具
 * 使用阿里云 DashScope Rerank API（qwen3-rerank）
 * 与 LLM、Embedding 共用同一套 API Key，无需额外配置
 *
 * @author aceFelix
 */
@Slf4j
@Component
public class RerankUtil {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private final String apiKey;
    private final String rerankUrl;
    private final String model;

    public RerankUtil(OkHttpClient httpClient,
                      ObjectMapper objectMapper,
                      @Value("${DASHSCOPE_API_KEY:}") String apiKey,
                      @Value("${dashscope.rerank.url:https://dashscope.aliyuncs.com/api/v1/services/rerank/text-rerank/text-rerank}") String rerankUrl,
                      @Value("${dashscope.rerank.model:qwen3-rerank}") String model) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.rerankUrl = rerankUrl;
        this.model = model;
    }

    /**
     * 对向量检索结果进行重排序
     * @param query      用户问题
     * @param documents  向量检索返回的文档列表
     * @param topN       精排后保留的文档数量
     * @return 重排序后的文档列表
     */
    public List<Document> rerank(String query, List<Document> documents, int topN) {
        if (documents == null || documents.isEmpty()) {
            return Collections.emptyList();
        }
        if (documents.size() <= topN) {
            return documents;
        }
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("DASHSCOPE_API_KEY 未配置，回退到原始排序，取 top {}", topN);
            return documents.subList(0, Math.min(topN, documents.size()));
        }

        try {
            List<String> texts = documents.stream()
                    .map(Document::getText)
                    .collect(Collectors.toList());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            Map<String, Object> input = new HashMap<>();
            input.put("query", query);
            input.put("documents", texts);
            requestBody.put("input", input);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("top_n", topN);
            requestBody.put("parameters", parameters);

            String json = objectMapper.writeValueAsString(requestBody);

            Request request = new Request.Builder()
                    .url(rerankUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(json, MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Rerank API 调用失败: HTTP {}, 回退到原始排序", response.code());
                    return documents.subList(0, Math.min(topN, documents.size()));
                }

                String responseBody = response.body().string();
                JsonNode root = objectMapper.readTree(responseBody);
                JsonNode results = root.get("output").get("results");

                Map<Integer, Double> scoreMap = new HashMap<>();
                List<Integer> sortedIndices = new ArrayList<>();
                for (JsonNode result : results) {
                    int index = result.get("index").asInt();
                    double score = result.get("relevance_score").asDouble();
                    sortedIndices.add(index);
                    scoreMap.put(index, score);
                }
                sortedIndices.sort((a, b) -> Double.compare(scoreMap.get(b), scoreMap.get(a)));

                if (log.isDebugEnabled()) {
                    String topScores = sortedIndices.stream()
                            .limit(Math.min(5, sortedIndices.size()))
                            .map(i -> String.format("%.3f", scoreMap.get(i)))
                            .collect(Collectors.joining(", "));
                    log.debug("Rerank({}) 完成: {}条 → {}条, top5得分: {}", model, documents.size(), topN, topScores);
                }

                return sortedIndices.stream()
                        .limit(topN)
                        .map(documents::get)
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            log.error("Rerank 异常，回退到原始排序: {}", e.getMessage());
            return documents.subList(0, Math.min(topN, documents.size()));
        }
    }
}
