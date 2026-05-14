package com.itfelix.liuhaizhuaichat.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * 请求日志拦截器
 * 记录每个请求的耗时和基本信息
 * @author aceFelix
 */
@Slf4j
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";
    private static final String REQUEST_ID = "requestId";

    /**
     * 在请求处理之前执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        request.setAttribute(REQUEST_ID, requestId);
        request.setAttribute(START_TIME, System.currentTimeMillis());
        
        log.info("[{}] 请求开始: {} {} from {}", requestId, request.getMethod(), request.getRequestURI(), getClientIp(request));
        return true;
    }

    /**
     * 在请求处理之后，视图渲染之前执行
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute(START_TIME);
        long duration = System.currentTimeMillis() - startTime;
        String requestId = (String) request.getAttribute(REQUEST_ID);
        
        String status = response.getStatus() < 400 ? "成功" : "失败";
        log.info("[{}] 请求结束: {} {} - 状态:{} 耗时:{}ms", 
                requestId, request.getMethod(), request.getRequestURI(), status, duration);
        
        // 慢请求告警
        if (duration > 3000) {
            log.warn("[{}] 慢请求告警: {} {} 耗时:{}ms", requestId, request.getMethod(), request.getRequestURI(), duration);
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
