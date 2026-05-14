package com.itfelix.liuhaizhuaichat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 搜索引擎返回结果
 * @author aceFleix
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearXNGResult {
    private String query;
    private List<WebSearchResult> results;
}
