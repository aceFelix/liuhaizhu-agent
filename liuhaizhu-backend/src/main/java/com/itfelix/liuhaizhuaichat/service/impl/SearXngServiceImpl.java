package com.itfelix.liuhaizhuaichat.service.impl;

import cn.hutool.json.JSONUtil;
import com.itfelix.liuhaizhuaichat.pojo.dto.SearXNGResult;
import com.itfelix.liuhaizhuaichat.pojo.dto.WebSearchResult;
import com.itfelix.liuhaizhuaichat.service.SearXngService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 联网搜索服务实现
 * @author aceFelix
 */
@Service
@Slf4j
public class SearXngServiceImpl implements SearXngService {
    @Value("${websearch.searxng.url}")
    private  String SEARXNG_URL;

    @Value("${websearch.searxng.count}")
    private  Integer COUNT;

    @Autowired
    private  OkHttpClient okHttpClient;

    /**
     * 使用SearXNG实现联网搜索
     * @param query
     * @return
     */
    @Override
    public List<WebSearchResult> search(String query) {
        // 构建url
        HttpUrl url = HttpUrl.get(SEARXNG_URL)
                .newBuilder()
                .addQueryParameter("q", query)
                .addQueryParameter("format", "json")
                .build();
        log.info("搜索的url为: {}", url.url());

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 发送请求
        try (Response response = okHttpClient.newCall(request).execute()){
            // 判断请求是否成功
            if (!response.isSuccessful()) {
                log.error("请求失败: {}, {}",response.message(), response.code());
                throw new RuntimeException("请求失败:" + response.code());
            }
            log.info("请求成功: {}, {}", response.message(), response.code());
            // 获得响应数据
            if (response.body() != null){
                String responseBody = response.body().string();
                log.info("原始响应: {}", JSONUtil.formatJsonStr(responseBody));
                // 解析响应数据，构建响应结果类
                SearXNGResult searXNGResult = JSONUtil.toBean(responseBody, SearXNGResult.class);
                log.info("Query: {}", searXNGResult.getQuery());
                // 筛选结果
                if (searXNGResult.getResults() != null && !searXNGResult.getResults().isEmpty()) {
                    log.info("结果数: {}", COUNT );
                    log.info("第一条结果: {}", searXNGResult.getResults().get(0));
                    return searXNGResult
                            .getResults()
                            .subList(0, Math.min(COUNT, searXNGResult.getResults().size()))
                            .parallelStream()
                            .sorted(Comparator.comparingDouble(WebSearchResult::getScore).reversed())
                            .limit(COUNT)
                            .toList();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Collections.emptyList();
    }
}
