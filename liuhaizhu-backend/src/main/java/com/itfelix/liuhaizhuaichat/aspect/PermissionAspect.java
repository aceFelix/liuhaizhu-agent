package com.itfelix.liuhaizhuaichat.aspect;

import com.itfelix.liuhaizhuaichat.annotation.RequirePermission;
import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;
import com.itfelix.liuhaizhuaichat.service.PermissionService;
import com.itfelix.liuhaizhuaichat.utils.AceResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 
 * 权限切面 - 处理@RequirePermission注解
 * @author aceFelix
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionAspect {

    private final PermissionService permissionService;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        // 获取当前请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String requestURI = attributes.getRequest().getRequestURI();
            // 跳过错误页面的权限检查
            if (requestURI != null && requestURI.startsWith("/error")) {
                log.debug("跳过错误页面的权限检查: {}", requestURI);
                return joinPoint.proceed();
            }
        }
        
        // 获取当前用户角色
        UserRoleEnum userRole = getCurrentUserRole();
        
        log.debug("权限检查 - 方法: {}, 用户角色: {}", joinPoint.getSignature(), userRole);

        if (userRole == null) {
            log.warn("未登录用户尝试访问受保护资源: {}", joinPoint.getSignature());
            return handleUnauthorized("请先登录");
        }

        // 检查是否被排除
        if (permissionService.isExcluded(userRole, requirePermission.excludedRoles())) {
            log.warn("用户角色 {} 被排除访问: {}", userRole, joinPoint.getSignature());
            return handleUnauthorized(requirePermission.message());
        }

        // 检查是否需要管理员权限
        if (requirePermission.admin()) {
            if (userRole != UserRoleEnum.ADMIN) {
                log.warn("用户角色 {} 无管理员权限访问: {}", userRole, joinPoint.getSignature());
                return handleUnauthorized("需要管理员权限");
            }
        }

        // 检查是否需要VIP权限
        if (requirePermission.vip()) {
            if (userRole != UserRoleEnum.ADMIN && userRole != UserRoleEnum.VIP) {
                log.warn("用户角色 {} 无VIP权限访问: {}", userRole, joinPoint.getSignature());
                return handleUnauthorized("需要VIP权限");
            }
        }

        // 检查是否有任何允许的角色
        if (requirePermission.allowedRoles().length > 0) {
            if (!permissionService.hasAnyRole(userRole, requirePermission.allowedRoles())) {
                log.warn("用户角色 {} 无权限访问: {}", userRole, joinPoint.getSignature());
                return handleUnauthorized(requirePermission.message());
            }
        }

        // 检查细粒度权限
        if (requirePermission.permissions().length > 0) {
            for (var permission : requirePermission.permissions()) {
                if (!permissionService.hasPermission(userRole, permission)) {
                    log.warn("用户角色 {} 缺少权限 {}: {}", userRole, permission, joinPoint.getSignature());
                    return handleUnauthorized(requirePermission.message());
                }
            }
        }

        // 权限检查通过，执行原方法
        return joinPoint.proceed();
    }

    /**
     * 获取当前用户角色
     */
    private UserRoleEnum getCurrentUserRole() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.debug("获取用户角色 - authentication: {}, isAuthenticated: {}", 
                    authentication, authentication != null ? authentication.isAuthenticated() : "null");
            
            if (authentication == null || !authentication.isAuthenticated()) {
                log.debug("用户未认证或认证对象为空");
                return null;
            }
    
            Object principal = authentication.getPrincipal();
            log.debug("获取用户角色 - principal 类型: {}", principal != null ? principal.getClass().getName() : "null");
            
            if (principal instanceof com.itfelix.liuhaizhuaichat.security.CustomUserDetails) {
                UserRoleEnum role = ((com.itfelix.liuhaizhuaichat.security.CustomUserDetails) principal).getRole();
                log.debug("获取用户角色成功: {}", role);
                return role;
            }
    
            // 匿名用户或无法识别的 principal，返回 null
            String username = authentication.getName();
            log.warn("无法从 principal 获取用户角色，principal 类型: {}, 用户名: {}", 
                    principal != null ? principal.getClass().getName() : "null", username);
            return null;
        } catch (Exception e) {
            log.error("获取用户角色失败", e);
            return null;
        }
    }

    /**
     * 处理未授权访问
     */
    private Object handleUnauthorized(String message) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletResponse response = attributes.getResponse();
                if (response != null) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":403,\"message\":\"" + message + "\"}");
                    return null;
                }
            }
        } catch (IOException e) {
            log.error("返回未授权响应失败", e);
        }
        return AceResult.error(403, message);
    }
}
