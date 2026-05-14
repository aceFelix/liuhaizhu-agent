package com.itfelix.liuhaizhuaichat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 联网搜索结果
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WebSearchResult {
    private String title;
    private String url;
    private String content;
    private Double score;
}
