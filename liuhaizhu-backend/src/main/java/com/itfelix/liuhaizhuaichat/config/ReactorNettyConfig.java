package com.itfelix.liuhaizhuaichat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Reactor Netty 连接池配置
 * 解决长时间不调用AI后首次请求 Connection reset by peer
 * <p>
 * 原因：默认连接池不限制空闲时长，DashScope 服务端关闭连接后，
 * 客户端池中仍保留僵尸连接，下次复用导致 Connection reset。
 * <p>
 * 修复：
 * 1. 通过系统属性设置 Reactor Netty 连接池的空闲超时和最大生命周期，主动淘汰僵尸连接
 * 2. 配合 ChatServiceImpl 中的 retryWhen 重试机制，兜底处理清理窗口期内的残留僵尸连接
 * <p>
 * 使用 static 块确保在 Spring 容器初始化、连接池创建之前，系统属性就已生效
 *
 * @author aceFelix
 */
@Slf4j
@Configuration
public class ReactorNettyConfig {

    static {
        // 连接池空闲超时（毫秒）：连接空闲超过此时间将被淘汰
        // 设为 2 分钟，远小于 DashScope 服务端的空闲超时，确保客户端先淘汰
        System.setProperty("reactor.netty.pool.maxIdleTime", "120000");
        // 连接最大生命周期（毫秒）：无论是否活跃，超过此时间强制淘汰
        // 设为 30 分钟，防止长期复用同一连接
        System.setProperty("reactor.netty.pool.maxLifeTime", "1800000");
        // 后台清理间隔（毫秒）：每隔 30 秒扫描并清理过期连接
        // 缩短间隔以更快发现僵尸连接
        System.setProperty("reactor.netty.pool.evictInBackground", "30000");
        // 获取连接超时（毫秒）：从池中获取连接的最大等待时间
        System.setProperty("reactor.netty.pool.pendingAcquireTimeout", "30000");
        // 合并 HTTP 头部验证（DashScope 可能返回非标准头，设为 false 更宽容）
        System.setProperty("reactor.netty.http.validateHeaders", "false");

        log.info("Reactor Netty 连接池参数已配置: maxIdleTime=120s, maxLifeTime=1800s, evictInBackground=30s");
    }
}
