package com.itfelix.liuhaizhuaichat.utils;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RecursiveTextSplitter 测试类
 * 
 * @author aceFelix
 */
class RecursiveTextSplitterTest {

    @Test
    void testBasicSplit() {
        String text = "这是第一段内容。\n\n这是第二段内容，稍微长一点。\n\n这是第三段内容。";
        
        RecursiveTextSplitter splitter = new RecursiveTextSplitter(100, 20);
        List<String> chunks = splitter.splitText(text);
        
        System.out.println("=== 基础分片测试 ===");
        System.out.println("原文长度: " + text.length());
        System.out.println("分片数量: " + chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("--- 片段 " + (i + 1) + " (长度: " + chunks.get(i).length() + ") ---");
            System.out.println(chunks.get(i));
        }
        
        assertFalse(chunks.isEmpty());
    }

    @Test
    void testLongParagraphSplit() {
        String text = "这是一个非常长的段落，包含了很多内容，需要被拆分成多个片段。" +
                "这个段落继续延伸，不断增加内容，直到超过chunkSize的限制。" +
                "这样我们就可以测试分片器是否能够正确地处理长段落的情况。" +
                "继续添加更多内容来确保这个段落足够长。" +
                "再添加一些内容，让这个段落变得更加冗长。" +
                "最后再添加一些内容来完成这个测试用例。";
        
        RecursiveTextSplitter splitter = new RecursiveTextSplitter(50, 10);
        List<String> chunks = splitter.splitText(text);
        
        System.out.println("\n=== 长段落分片测试 ===");
        System.out.println("原文长度: " + text.length());
        System.out.println("分片数量: " + chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("--- 片段 " + (i + 1) + " (长度: " + chunks.get(i).length() + ") ---");
            System.out.println(chunks.get(i));
        }
        
        assertTrue(chunks.size() > 1);
    }

    @Test
    void testOverlapMechanism() {
        String text = "第一段内容。第二段内容。第三段内容。第四段内容。第五段内容。";
        
        RecursiveTextSplitter splitter = new RecursiveTextSplitter(30, 10);
        List<String> chunks = splitter.splitText(text);
        
        System.out.println("\n=== 重叠机制测试 ===");
        System.out.println("原文: " + text);
        System.out.println("分片数量: " + chunks.size());
        
        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("--- 片段 " + (i + 1) + " ---");
            System.out.println(chunks.get(i));
            
            if (i > 0) {
                String prevChunk = chunks.get(i - 1);
                String currChunk = chunks.get(i);
                
                boolean hasOverlap = false;
                for (int j = 5; j <= 15; j++) {
                    if (prevChunk.length() >= j) {
                        String overlapCandidate = prevChunk.substring(prevChunk.length() - j);
                        if (currChunk.startsWith(overlapCandidate) || 
                            currChunk.contains(overlapCandidate.substring(0, Math.min(overlapCandidate.length(), 5)))) {
                            hasOverlap = true;
                            break;
                        }
                    }
                }
                
                if (hasOverlap) {
                    System.out.println("✓ 与上一片段存在重叠");
                }
            }
        }
    }

    @Test
    void testMixedContent() {
        String text = "# 标题\n\n" +
                "这是第一段内容，包含了一些重要信息。\n\n" +
                "这是第二段内容，包含以下要点：\n" +
                "1. 第一个要点\n" +
                "2. 第二个要点\n" +
                "3. 第三个要点\n\n" +
                "这是第三段内容，作为总结。";
        
        RecursiveTextSplitter splitter = new RecursiveTextSplitter(80, 15);
        List<String> chunks = splitter.splitText(text);
        
        System.out.println("\n=== 混合内容分片测试 ===");
        System.out.println("原文长度: " + text.length());
        System.out.println("分片数量: " + chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("--- 片段 " + (i + 1) + " (长度: " + chunks.get(i).length() + ") ---");
            System.out.println(chunks.get(i));
            System.out.println();
        }
    }

    @Test
    void testApplyWithDocuments() {
        Document doc = new Document("这是测试文档内容。\n\n包含多个段落。\n\n每个段落都应该被正确处理。");
        
        RecursiveTextSplitter splitter = new RecursiveTextSplitter(50, 10);
        List<Document> chunks = splitter.apply(List.of(doc));
        
        System.out.println("\n=== Document对象分片测试 ===");
        System.out.println("分片数量: " + chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("--- 片段 " + (i + 1) + " ---");
            System.out.println(chunks.get(i).getText());
        }
        
        assertFalse(chunks.isEmpty());
    }

    @Test
    void testChineseSentenceSplit() {
        String text = "这是第一句话。这是第二句话！这是第三句话？这是第四句话；这是第五句话，包含逗号。";
        
        RecursiveTextSplitter splitter = new RecursiveTextSplitter(25, 5);
        List<String> chunks = splitter.splitText(text);
        
        System.out.println("\n=== 中文句子分片测试 ===");
        System.out.println("原文: " + text);
        System.out.println("分片数量: " + chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("--- 片段 " + (i + 1) + " ---");
            System.out.println(chunks.get(i));
        }
    }

    @Test
    void testEdgeCases() {
        RecursiveTextSplitter splitter = new RecursiveTextSplitter(50, 10);
        
        System.out.println("\n=== 边界情况测试 ===");
        
        List<String> emptyResult = splitter.splitText("");
        System.out.println("空字符串: " + emptyResult.size() + " 个片段");
        assertTrue(emptyResult.isEmpty());
        
        List<String> nullResult = splitter.splitText(null);
        System.out.println("null: " + nullResult.size() + " 个片段");
        assertTrue(nullResult.isEmpty());
        
        String shortText = "短文本";
        List<String> shortResult = splitter.splitText(shortText);
        System.out.println("短文本: " + shortResult.size() + " 个片段");
        assertEquals(1, shortResult.size());
        assertEquals(shortText, shortResult.get(0));
    }

    @Test
    void testRealWorldDocument() {
        String text = """
                # 产品使用手册
                
                ## 第一章 产品介绍
                
                本产品是一款智能对话助手，具有以下核心功能：
                
                1. 智能问答：基于大语言模型，能够理解用户问题并给出准确回答。
                2. 知识库检索：支持上传文档构建知识库，实现精准的知识问答。
                3. 联网搜索：实时获取互联网信息，确保回答的时效性。
                
                ## 第二章 使用指南
                
                ### 2.1 注册与登录
                
                用户可以通过以下方式注册账号：
                - 手机号注册
                - 邮箱注册
                - 第三方账号登录（微信、QQ等）
                
                ### 2.2 知识库管理
                
                VIP用户可以上传文档到知识库。支持以下格式：
                - TXT文本文件
                - PDF文档
                - Word文档
                
                上传后的文档会自动进行文本分片处理，每个片段大小约为500字符，片段之间有50字符的重叠，以确保检索的准确性。
                
                ## 第三章 常见问题
                
                Q: 如何提高检索准确率？
                A: 建议上传结构清晰的文档，避免过长的段落，可以使用标题和分段来组织内容。
                
                Q: 支持哪些语言？
                A: 目前支持中文和英文，其他语言的支持正在开发中。
                """;
        
        RecursiveTextSplitter splitter = new RecursiveTextSplitter(200, 40);
        List<String> chunks = splitter.splitText(text);
        
        System.out.println("\n=== 真实文档分片测试 ===");
        System.out.println("原文长度: " + text.length() + " 字符");
        System.out.println("分片数量: " + chunks.size());
        System.out.println();
        
        int totalChunkLength = 0;
        for (int i = 0; i < chunks.size(); i++) {
            int len = chunks.get(i).length();
            totalChunkLength += len;
            System.out.println("--- 片段 " + (i + 1) + " (长度: " + len + ") ---");
            System.out.println(chunks.get(i));
            System.out.println();
        }
        
        System.out.println("总片段长度: " + totalChunkLength);
        System.out.println("重叠比例: " + String.format("%.1f%%", (totalChunkLength - text.length()) * 100.0 / text.length()));
    }
}
