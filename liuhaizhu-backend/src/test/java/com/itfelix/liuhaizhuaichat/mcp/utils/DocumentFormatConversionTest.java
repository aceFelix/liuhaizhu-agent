package com.itfelix.liuhaizhuaichat.mcp.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DocumentFormatConversion 单元测试
 * @author aceFelix
 */
@DisplayName("DocumentFormatConversion 单元测试")
class DocumentFormatConversionTest {

    private final DocumentFormatConversion converter = new DocumentFormatConversion();

    @Nested
    @DisplayName("convertToHtml - Markdown 转 HTML")
    class ConvertToHtmlTests {

        @Test
        @DisplayName("应正确转换标题")
        void shouldConvertHeading() {
            String result = converter.convertToHtml("# 标题");

            assertTrue(result.contains("标题"));
            // flexmark 将 # 转换为 <h1>
            assertTrue(result.contains("<h1>") || result.contains("h1"));
        }

        @Test
        @DisplayName("应正确转换加粗文本")
        void shouldConvertBold() {
            String result = converter.convertToHtml("**加粗**");

            assertTrue(result.contains("加粗"));
            assertTrue(result.contains("<strong>") || result.contains("strong"));
        }

        @Test
        @DisplayName("应正确转换链接")
        void shouldConvertLink() {
            String result = converter.convertToHtml("[链接](https://example.com)");

            assertTrue(result.contains("链接"));
            assertTrue(result.contains("example.com"));
        }

        @Test
        @DisplayName("应正确转换无序列表")
        void shouldConvertUnorderedList() {
            String result = converter.convertToHtml("- 项目1\n- 项目2");

            assertTrue(result.contains("项目1"));
            assertTrue(result.contains("项目2"));
            assertTrue(result.contains("<li>") || result.contains("<ul>"));
        }

        @Test
        @DisplayName("空字符串应返回空 HTML（包装标签）")
        void shouldHandleEmptyString() {
            String result = converter.convertToHtml("");
            assertNotNull(result);
        }

        @Test
        @DisplayName("普通文本应保持内容")
        void shouldPreservePlainText() {
            String result = converter.convertToHtml("这是普通文本");

            assertTrue(result.contains("这是普通文本"));
        }
    }
}
