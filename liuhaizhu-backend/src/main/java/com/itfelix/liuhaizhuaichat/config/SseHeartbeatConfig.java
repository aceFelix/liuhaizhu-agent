package com.itfelix.liuhaizhuaichat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static com.itfelix.liuhaizhuaichat.utils.SSEServerUtil.getSseClients;

/**
 * SSE心跳配置
 * 定期向客户端发送心跳消息，保持连接活跃
 * @author aceFelix
 */
@Slf4j
@Configuration
@EnableScheduling
public class SseHeartbeatConfig {

    private static final String HEARTBEAT_MESSAGE = "heartbeat";

    /**
     * 每30秒发送一次心跳
     */
    @Scheduled(fixedRate = 30000)
    public void sendHeartbeat() {
        Map<String, SseEmitter> clients = getSseClients();
        if (clients == null || clients.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<String, SseEmitter>> iterator = clients.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SseEmitter> entry = iterator.next();
            String userId = entry.getKey();
            SseEmitter emitter = entry.getValue();
            
            try {
                emitter.send(SseEmitter.event()
                        .name("heartbeat")
                        .data(HEARTBEAT_MESSAGE));
                log.debug("SSE心跳发送成功: userId={}", userId);
            } catch (IOException e) {
                log.warn("SSE心跳发送失败，移除连接: userId={}", userId);
                iterator.remove();
            }
        }
    }

    /**
     * 每小时清理超时的SSE连接
     */
    @Scheduled(fixedRate = 3600000)
    public void cleanupStaleConnections() {
        Map<String, SseEmitter> clients = getSseClients();
        if (clients != null) {
            log.info("当前活跃SSE连接数: {}", clients.size());
        }
    }
}
