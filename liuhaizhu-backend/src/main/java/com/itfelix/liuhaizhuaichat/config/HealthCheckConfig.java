package com.itfelix.liuhaizhuaichat.config;

import com.itfelix.liuhaizhuaichat.utils.SSEServerUtil;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 健康检查配置
 * 提供系统状态监控端点
 * @author aceFelix
 */
@Configuration
public class HealthCheckConfig {

    /**
     * Redis健康检查
     */
    @Bean
    public HealthIndicator redisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        return () -> {
            try {
                redisConnectionFactory.getConnection().ping();
                return Health.up().withDetail("redis", "connected").build();
            } catch (Exception e) {
                return Health.down().withDetail("redis", "disconnected").withException(e).build();
            }
        };
    }

    /**
     * SSE连接健康检查
     */
    @Bean
    public HealthIndicator sseHealthIndicator() {
        return () -> {
            int connectionCount = SSEServerUtil.getConnectionCount();
            return Health.up()
                    .withDetail("sseConnections", connectionCount)
                    .withDetail("status", "active")
                    .build();
        };
    }
}
