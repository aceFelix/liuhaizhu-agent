package com.itfelix.liuhaizhuaichat.mcp;

import com.itfelix.liuhaizhuaichat.enums.UserRoleEnum;
import com.itfelix.liuhaizhuaichat.mcp.tool.DateTool;
import com.itfelix.liuhaizhuaichat.mcp.tool.EmailTool;
import com.itfelix.liuhaizhuaichat.mcp.tool.ProductTool;
import com.itfelix.liuhaizhuaichat.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 注册MCP工具 - 根据用户角色动态过滤工具
 * @author aceFelix
 */
@Component
@Slf4j
public class MyToolCallbackProvider {

    /**
     * 检查当前用户是否已登录
     * 注意：此方法在请求时调用，而不是启动时
     */
    public static boolean isLoggedIn() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                return principal instanceof CustomUserDetails;
            }
        } catch (Exception e) {
            log.error("获取用户登录状态失败", e);
        }
        return false;
    }

    /**
     * 整合MCP工具 自定义工具+外部MCP工具
     * 所有工具都注册，权限控制在工具内部实现
     * @param dateTool
     * @param emailTool
     * @param mcpAsyncToolCallbacks
     * @return
     */
    @Bean
    @Primary
    public ToolCallbackProvider getToolCallbacks(
            DateTool dateTool,
            EmailTool emailTool,
            ProductTool productTool,
            @Qualifier("mcpAsyncToolCallbacks") ToolCallbackProvider mcpAsyncToolCallbacks) {
        
        log.info("注册MCP工具：DateTool, EmailTool, ProductTool");

        // 所有工具都注册，权限检查在工具方法内部进行
        MethodToolCallbackProvider customTools = MethodToolCallbackProvider.builder()
                .toolObjects(dateTool, emailTool, productTool)
                .build();
        
        return () -> {
            List<ToolCallback> allCallbacks = new ArrayList<>();
            allCallbacks.addAll(List.of(customTools.getToolCallbacks()));
            
            // 外部MCP工具也注册，权限检查在调用时进行
            allCallbacks.addAll(List.of(mcpAsyncToolCallbacks.getToolCallbacks()));
            
            return allCallbacks.toArray(new ToolCallback[0]);
        };
    }
}
