package com.itfelix.liuhaizhuaichat.config;

import com.itfelix.liuhaizhuaichat.security.CustomUserDetailsService;
import com.itfelix.liuhaizhuaichat.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.annotation.PostConstruct;

import java.util.Arrays;

/**
 * 安全配置类
 * 配置Spring Security的安全策略，包括CORS、CSRF、会话管理、权限验证等
 * 
 * @author aceFelix
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置SecurityContextHolder的策略，使其在异步线程中也能获取到当前用户
     */
    @PostConstruct
    public void enableSecurityContextForAsync() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    /**
     * 安全过滤器链
     * 配置HTTP安全设置，包括禁用CSRF、配置CORS、会话管理、请求匹配和认证提供者
     * 
     * @param http HTTP安全配置对象
     * @return 配置后的安全过滤器链
     * @throws Exception 如果配置过程中发生异常
     */
    @Bean
    
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/sse/**").permitAll()
                        .requestMatchers("/api/sse/**").permitAll()
                        .requestMatchers("/api/chat/doChat").permitAll()
                        .requestMatchers("/api/webSearch/query").permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 认证提供者
     * 配置基于数据库的用户详情服务和密码编码器
     * 
     * @return 配置后的认证提供者
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // 使用构造函数注入
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    /**
     * 认证管理器
     * 配置基于AuthenticationConfiguration的认证管理器
     * 
     * @param config 认证配置对象
     * @return 配置后的认证管理器
     * @throws Exception 如果配置过程中发生异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 密码编码器
     * 配置BCrypt密码编码器，用于对用户密码进行加密存储
     * 
     * @return 配置后的密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS配置源
     * 配置跨域资源共享（CORS）策略，允许所有来源、所有方法和所有头
     * 
     * @return 配置后的CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有来源（使用 allowedOriginPatterns 支持通配符）
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        // 或者明确指定允许的域名
         /*configuration.setAllowedOrigins(Arrays.asList(
             "http://localhost:5173",
             "http://127.0.0.1:5173",
         ));*/
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-User-Id"));
        // 当使用 allowedOriginPatterns 时，allowCredentials 可以正常工作
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
