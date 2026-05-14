package com.itfelix.liuhaizhuaichat.security;

import com.itfelix.liuhaizhuaichat.mapper.UserMapper;
import com.itfelix.liuhaizhuaichat.pojo.entity.User;
import com.itfelix.liuhaizhuaichat.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * JWT认证过滤器类
 * 继承OncePerRequestFilter，用于在每个请求中验证JWT token
 * @author aceFelix
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    /**
     * 对每个请求进行过滤，验证JWT token
     * 如果token有效，将用户信息设置到SecurityContextHolder中
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param filterChain 过滤器链对象
     * @throws ServletException 如果处理请求时发生异常
     * @throws IOException 如果处理I/O操作时发生异常
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String token = extractTokenFromRequest(request);
        
        log.debug("JWT过滤器 - 请求: {} {}, token是否存在: {}", method, requestURI, StringUtils.hasText(token));

        // 验证必须是有效的 accessToken，防止 refreshToken 冒充
        if (StringUtils.hasText(token)) {
            boolean isValid = jwtUtil.validateAccessToken(token);
            log.debug("JWT过滤器 - token验证结果: {}", isValid);
            
            if (isValid) {
                // 从token中获取userId（subject是userId）
                String userId = jwtUtil.getUserIdFromToken(token);
                log.debug("JWT过滤器 - 从token获取userId: {}", userId);
                
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 通过userId加载用户
                    User user = userMapper.selectById(userId);
                    
                    if (user != null) {
                        CustomUserDetails userDetails = new CustomUserDetails(user);
                        
                        // 已在上面验证过token，此处无需重复验证
                        UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("JWT过滤器 - 认证成功设置到SecurityContext, userId: {}, role: {}", userId, userDetails.getRole());
                    } else {
                        log.warn("JWT过滤器 - 未找到用户, userId: {}", userId);
                    }
                }
            }
        } else {
            log.debug("JWT过滤器 - 请求中未找到token");
        }

        filterChain.doFilter(request, response);
    }
    /**
     * 从HTTP请求中提取JWT token
     * 从Authorization头中提取Bearer token
     * @param request HTTP请求对象
     * @return 提取到的JWT token，如果不存在则返回null
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
