package com.itfelix.liuhaizhuaichat.enums;

/**
 * SSE消息类型枚举
 * @author aceFelix
 */
public enum SSEMessageType {
    MESSAGE("message","单次发送的普通类型消息"),
    ADD("add","消息追加，适用于流式stream推送"),
    FINISH("finish","消息完成"),
    ERROR("error","错误消息"),
    TITLE_UPDATE("title_update","会话标题更新"),
    CUSTOM_EVENT("custom-event","单次发送的普通事件"),
    DONE("done","单次发送的普通类型消息"),
    CONNECTED("connected","连接建立确认");

    public final String type;
    public final String value;

    SSEMessageType(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
