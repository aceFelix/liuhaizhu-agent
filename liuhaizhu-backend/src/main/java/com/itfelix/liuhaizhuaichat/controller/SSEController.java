package com.itfelix.liuhaizhuaichat.controller;

import com.itfelix.liuhaizhuaichat.utils.JwtUtil;
import com.itfelix.liuhaizhuaichat.utils.SSEServerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE连接控制器
 * @author aceFelix
 */
@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SSEController {
    
    private final JwtUtil jwtUtil;
    
    /**
     * 建立SSE连接
     * @param userId 用户ID
     * @param token JWT token（从URL参数传递，因为EventSource不支持自定义请求头）
     * @return SseEmitter
     */
    @GetMapping("/connect")
    public SseEmitter connect(@RequestParam String userId, @RequestParam(required = false) String token) {
        if (token != null && !token.isEmpty()) {
            if (!jwtUtil.validateAccessToken(token)) {
                throw new RuntimeException("Invalid token");
            }
        }
        return SSEServerUtil.connect(userId);
    }
}
