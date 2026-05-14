package com.itfelix.liuhaizhuaichat.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * @author aceFelix
 */
@Slf4j
@Component
@Aspect
public class LogAspect {
    /**
     * 日志切面 环绕通知
     *         *：表示返回任意类型
     *         com.itfelix.agentchat.service.impl：指定包名，要切class类的所在包
     *         ..：可以匹配当前包下的所有子包下的所有类
     *         *：可以匹配当前包以及子包下的class类
     *         .：无意义
     *         *：匹配任意方法名
     *         (..)：匹配方法任意参数
     * @param joinPoint
     * @return
     */
    @Around("execution(* com.itfelix.liuhaizhuaichat.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // Long start = System.currentTimeMillis();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 执行被拦截的目标方法
        Object proceed = joinPoint.proceed();
        // 获取目标类名和方法名
        String point = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
        // Long end = System.currentTimeMillis();
        stopWatch.stop();
        // 计算方法执行好使
        Long takeTime = stopWatch.getTotalTimeMillis();
        if (takeTime > 3000){
            log.error("[方法：{}][耗时：{}ms]，耗时偏长", point, takeTime);
        }else if (takeTime > 2000){
            log.warn("[方法：{}][耗时：{}ms]，耗时中等", point, takeTime);
        }else {
            log.info("[方法：{}][耗时：{}ms]，正常耗时", point, takeTime);
        }
        return proceed;
    }
}
