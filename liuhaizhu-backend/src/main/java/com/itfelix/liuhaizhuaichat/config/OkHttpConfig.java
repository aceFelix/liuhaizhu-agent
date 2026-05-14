package com.itfelix.liuhaizhuaichat.config;

import com.itfelix.liuhaizhuaichat.interceptor.RequestLogInterceptor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * Web配置类
 * @author aceFelix
 */
@Configuration
public class OkHttpConfig implements WebMvcConfigurer {
    
    @Autowired
    private RequestLogInterceptor requestLogInterceptor;
    
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加请求日志拦截器
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/api/**")
                // SSE连接不记录
                .excludePathPatterns("/api/sse/**");
    }
}
