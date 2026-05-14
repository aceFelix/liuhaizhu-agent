package com.itfelix.liuhaizhuaichat.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AceResult<T> {
    // 响应状态码
    private Integer code;
    // 响应消息
    private String message;
    // 响应数据
    private T data;

    public AceResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> AceResult<T> success(T data) {
        return new AceResult<>(200, "success", data);
    }

    public static <T> AceResult<T> success() {
        return new AceResult<>(200, "success");
    }

    public static <T> AceResult<T> error(String message) {
        return new AceResult<>(500, message);
    }

    public static <T> AceResult<T> error() {
        return new AceResult<>(500, "error");
    }

    public static <T> AceResult<T> error(T data) {
        return new AceResult<>(500, "error", data);
    }

    public static <T> AceResult<T> error(Integer code, String message) {
        return new AceResult<>(code, message);
    }

    public static <T> AceResult<T> error(Integer code, String message,T data) {
        return new AceResult<>(code, message, data);
    }

}
