package com.itfelix.liuhaizhuaichat.utils;

import com.itfelix.liuhaizhuaichat.enums.SSEMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * SSE服务
 * @author aceFelix
 */
@Slf4j
public class SSEServerUtil {
    // 存放所有用户
    private static final Map<String, SseEmitter> SSE_CLIENTS = new ConcurrentHashMap<>();

    /** 
     * 创建SSE连接
     * @param userId
     * @return
     */
    public static SseEmitter connect(String userId){
        SseEmitter sseEmitter = new SseEmitter(0L);

        sseEmitter.onTimeout(timeoutCallback(userId, sseEmitter));
        sseEmitter.onCompletion(completionCallback(userId, sseEmitter));
        sseEmitter.onError(errorCallback(userId, sseEmitter));

        SSE_CLIENTS.put(userId, sseEmitter);

        log.info("SSE连接创建成功...连接的用户id为:{}", userId);

        try {
            SseEmitter.SseEventBuilder connectedEvent = SseEmitter.event()
                    .id(userId)
                    .name(SSEMessageType.CONNECTED.type)
                    .data("connected");
            sseEmitter.send(connectedEvent);
            log.info("已发送connected确认事件给用户:{}", userId);
        } catch (IOException e) {
            log.error("发送connected确认事件失败: {}", e.getMessage());
            remove(userId, sseEmitter);
        }

        return sseEmitter;
    }

    /**
     * 发送单个消息
     * @param userId
     * @param message
     * @param messageType
     */
    public static void sendMessage(String userId, String message, SSEMessageType messageType){
        if(CollectionUtils.isEmpty(SSE_CLIENTS)) {
            return;
        }
        if (SSE_CLIENTS.containsKey(userId)){
            // 获取用户对应的sseEmitter对象
            SseEmitter sseEmitter = SSE_CLIENTS.get(userId);
            sendEmitterMessage(sseEmitter, userId, message, messageType);
        }
    }

    /**
     * 发送消息方法
     * @param sseEmitter
     * @param userId
     * @param message
     * @param messageType
     */
    private static void sendEmitterMessage(SseEmitter sseEmitter,String userId, String  message, SSEMessageType messageType){
        SseEmitter.SseEventBuilder msgEvent = SseEmitter.event()
                .id(userId)
                .name(messageType.type)
                .data(message);
        try {
            sseEmitter.send(msgEvent);
        } catch (IOException e) {
            log.error("SSE连接异常...{}",e.getMessage());
            remove(userId, sseEmitter);
        }
    }

    public static Runnable timeoutCallback(String userId, SseEmitter sseEmitter){
        return () -> {
            log.info("SSE连接超时...");
            remove(userId, sseEmitter);
        };
    }

    public static Runnable completionCallback(String userId, SseEmitter sseEmitter){
        return () -> {
            log.info("SSE连接完成...");
            remove(userId, sseEmitter);
        };
    }

    public static Consumer<Throwable> errorCallback(String userId, SseEmitter sseEmitter){
        return throwable -> {
            log.info("SSE连接异常...");
            remove(userId, sseEmitter);
        };
    }

    public static void remove(String userId, SseEmitter sseEmitter){
        SSE_CLIENTS.remove(userId, sseEmitter);
        log.info("SSE连接被移除...移除的用户id为:{}", userId);
    }

    /**
     * 获取所有SSE客户端
     * @return SSE客户端Map
     */
    public static Map<String, SseEmitter> getSseClients() {
        return SSE_CLIENTS;
    }

    /**
     * 获取当前连接数
     * @return 连接数
     */
    public static int getConnectionCount() {
        return SSE_CLIENTS.size();
    }

    /**
     * 检查用户是否在线
     * @param userId 用户ID
     * @return 是否在线
     */
    public static boolean isOnline(String userId) {
        return SSE_CLIENTS.containsKey(userId);
    }
}
