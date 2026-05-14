package com.itfelix.liuhaizhuaichat.utils;

import lombok.Getter;
import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.ArrayList;
import java.util.List;

/**
 * 递归字符文本拆分器
 * 
 * 按优先级递归尝试不同的分隔符，保持语义完整性的同时控制片段大小
 * 支持重叠机制，提高检索召回率
 * 
 * @author aceFelix
 */
public class RecursiveTextSplitter extends TextSplitter {

    // 分片大小，默认 500
    @Getter
    private final int chunkSize;
    // 分片重叠大小，默认 50
    @Getter
    private final int chunkOverlap;
    // 分隔符列表，默认按优先级递归尝试
    private final List<String> separators;

    /**
     * 构造函数
     *  默认分片大小 500，分片重叠大小 50
     */
    public RecursiveTextSplitter() {
        this(500, 50);
    }

    /**
     * 构造函数
     *
     * @param chunkSize    分片大小
     * @param chunkOverlap 分片重叠大小
     */
    public RecursiveTextSplitter(int chunkSize, int chunkOverlap) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
        this.separators = List.of(
                "\n\n",
                "\n",
                "。",
                "！",
                "？",
                "；",
                "，",
                " ",
                ""
        );
    }

    /**
     * 构造函数
     *
     * @param chunkSize    分片大小
     * @param chunkOverlap 分片重叠大小
     * @param separators   分隔符列表
     */
    public RecursiveTextSplitter(int chunkSize, int chunkOverlap, List<String> separators) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
        this.separators = separators;
    }

    /**
     * 文本拆分方法
     *
     * @param text 待拆分文本
     * @return 拆分后的文本列表
     */
    @Override
    protected List<String> splitText(String text) {
        return splitTextRecursive(text, separators);
    }

    /**
     * 递归文本拆分方法
     *
     * @param text          待拆分文本
     * @param separators    分隔符列表
     * @return 拆分后的文本列表
     */
    private List<String> splitTextRecursive(String text, List<String> separators) {
        List<String> finalChunks = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            return finalChunks;
        }

        int textLength = text.length();

        if (textLength <= chunkSize) {
            finalChunks.add(text.trim());
            return finalChunks;
        }

        String separator = getBestSeparator(text, separators);
        List<String> splits = splitBySeparator(text, separator);
        List<String> currentSeparatorList = separators.subList(
                separators.indexOf(separator) + 1,
                separators.size()
        );

        List<String> goodSplits = new ArrayList<>();
        int totalLength = 0;

        for (String split : splits) {
            int splitLength = split.length();

            if (splitLength > chunkSize) {
                if (!goodSplits.isEmpty()) {
                    List<String> mergedChunks = mergeSplits(goodSplits, separator);
                    finalChunks.addAll(mergedChunks);
                    goodSplits.clear();
                    totalLength = 0;
                }

                if (currentSeparatorList.isEmpty()) {
                    List<String> forcedChunks = forceSplit(split);
                    finalChunks.addAll(forcedChunks);
                } else {
                    List<String> recursiveChunks = splitTextRecursive(split, currentSeparatorList);
                    finalChunks.addAll(recursiveChunks);
                }
            } else if (totalLength + splitLength + (goodSplits.isEmpty() ? 0 : separator.length()) > chunkSize) {
                if (!goodSplits.isEmpty()) {
                    List<String> mergedChunks = mergeSplits(goodSplits, separator);
                    finalChunks.addAll(mergedChunks);
                    goodSplits.clear();
                    totalLength = 0;
                }
                goodSplits.add(split);
                totalLength = splitLength;
            } else {
                goodSplits.add(split);
                totalLength += splitLength + (goodSplits.size() > 1 ? separator.length() : 0);
            }
        }

        if (!goodSplits.isEmpty()) {
            List<String> mergedChunks = mergeSplits(goodSplits, separator);
            finalChunks.addAll(mergedChunks);
        }

        return finalChunks;
    }

    /**
     * 获取最佳分隔符
     *
     * @param text          待拆分文本
     * @param separators    分隔符列表
     * @return 最佳分隔符
     */
    private String getBestSeparator(String text, List<String> separators) {
        for (String separator : separators) {
            if (separator.isEmpty()) {
                return separator;
            }
            if (text.contains(separator)) {
                return separator;
            }
        }
        return separators.get(separators.size() - 1);
    }

    /**
     * 根据分隔符拆分文本
     *
     * @param text      待拆分文本
     * @param separator 分隔符
     * @return 拆分后的文本列表
     */
    private List<String> splitBySeparator(String text, String separator) {
        List<String> splits = new ArrayList<>();

        if (separator.isEmpty()) {
            int chunk = Math.max(1, chunkSize / 2);
            for (int i = 0; i < text.length(); i += chunk) {
                int end = Math.min(i + chunk, text.length());
                splits.add(text.substring(i, end));
            }
        } else {
            int start = 0;
            int index;
            while ((index = text.indexOf(separator, start)) != -1) {
                String part = text.substring(start, index + separator.length());
                if (!part.trim().isEmpty()) {
                    splits.add(part);
                }
                start = index + separator.length();
            }
            if (start < text.length()) {
                String remaining = text.substring(start);
                if (!remaining.trim().isEmpty()) {
                    splits.add(remaining);
                }
            }
        }

        return splits;
    }

    /**
     * 合并拆分后的文本
     *
     * @param splits    拆分后的文本列表
     * @param separator 分隔符
     * @return 合并后的文本列表
     */
    private List<String> mergeSplits(List<String> splits, String separator) {
        List<String> mergedChunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        int currentLength = 0;

        for (String split : splits) {
            int splitLength = split.length();
            int separatorLength = currentLength > 0 ? separator.length() : 0;
            int totalNewLength = currentLength + splitLength + separatorLength;

            if (totalNewLength > chunkSize && currentLength > 0) {
                String chunk = currentChunk.toString().trim();
                if (!chunk.isEmpty()) {
                    mergedChunks.add(chunk);
                }

                if (chunkOverlap > 0 && currentLength > chunkOverlap) {
                    String overlapText = getOverlapText(currentChunk.toString(), chunkOverlap);
                    currentChunk = new StringBuilder(overlapText);
                    currentLength = overlapText.length();
                } else {
                    currentChunk = new StringBuilder();
                    currentLength = 0;
                }
            }

            if (currentLength > 0) {
                currentChunk.append(separator);
            }
            currentChunk.append(split);
            currentLength = currentChunk.length();
        }

        String finalChunk = currentChunk.toString().trim();
        if (!finalChunk.isEmpty()) {
            mergedChunks.add(finalChunk);
        }

        return mergedChunks;
    }

    /**
     * 获取重叠文本
     *
     * @param text        文本
     * @param overlapSize 重叠大小
     * @return 重叠文本
     */
    private String getOverlapText(String text, int overlapSize) {
        if (text.length() <= overlapSize) {
            return text;
        }

        int start = text.length() - overlapSize;
        int spaceIndex = text.lastIndexOf(' ', start + overlapSize / 2);
        int newlineIndex = text.lastIndexOf('\n', start + overlapSize / 2);

        int actualStart = start;
        if (spaceIndex > start) {
            actualStart = spaceIndex + 1;
        } else if (newlineIndex > start) {
            actualStart = newlineIndex + 1;
        }

        return text.substring(actualStart);
    }

    /**
     * 强制拆分文本
     *
     * @param text 待拆分文本
     * @return 拆分后的文本列表
     */
    private List<String> forceSplit(String text) {
        List<String> chunks = new ArrayList<>();
        int length = text.length();
        int step = chunkSize - chunkOverlap;

        for (int i = 0; i < length; i += step) {
            int end = Math.min(i + chunkSize, length);
            chunks.add(text.substring(i, end));
        }

        return chunks;
    }

}
