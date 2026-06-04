package com.itfelix.liuhaizhuaichat.enums;

/**
 * SSE消息类型枚举
 * @author aceFelix
 */
public enum SSEMessageType {
    MESSAGE("message","单次发送的普通类型消息"),
    ADD("add","消息追加，适用于流式stream推送"),
    FINISH("finish","单次消息推送完成"),
    ERROR("error","消息推送错误"),
    TITLE_UPDATE("title_update","会话标题更新"),
    CUSTOM_EVENT("custom-event","自定义事件"),
    DONE("done","会话结束"),
    CONNECTED("connected","SSE连接建立确认");

    public final String type;
    public final String value;

    SSEMessageType(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
