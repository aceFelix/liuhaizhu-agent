package com.itfelix.liuhaizhuaichat.utils;

import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.List;

/**
 * @author aceFelix
 */
public class MyTokenTextSplitter extends TextSplitter {
    @Override
    protected List<String> splitText(String text) {
        return List.of(split(text));
    }

    public String[] split(String text) {
        // \s*\R\s*\R\s*: 匹配一个或多个空白字符，然后匹配一个或多个换行符，
        // 然后匹配一个或多个空白字符，然后匹配一个或多个换行符，然后匹配一个或多个空白字符
        return text.split("\\s*\\R\\s*\\R\\s*");
    }
}
