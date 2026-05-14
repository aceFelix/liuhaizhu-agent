package com.itfelix.liuhaizhuaichat.mcp.utils;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.springframework.stereotype.Component;

/**
 * 文档格式转换器
 * @author aceFelix
 */
@Component
public class DocumentFormatConversion {
    /**
     * markdown格式转换成html格式
     * @param markdownText
     * @return
     */
    public String convertToHtml(String markdownText){
        MutableDataSet dataSet = new MutableDataSet();
        Parser parser = Parser.builder(dataSet).build();
        HtmlRenderer renderer = HtmlRenderer.builder(dataSet).build();
        return renderer.render(parser.parse(markdownText));
    }
}
