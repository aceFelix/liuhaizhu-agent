package com.itfelix.liuhaizhuaichat.service;

import com.itfelix.liuhaizhuaichat.pojo.dto.WebSearchResult;

import java.util.List;

/**
 * @author aceFelix
 */
public interface SearXngService {

    /**
     * 调用本地搜索引擎searXNG进行搜索
     * @param query
     * @return
     */
    List<WebSearchResult> search(String query);
}
