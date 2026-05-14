package com.itfelix.liuhaizhuaichat.aspect;

import com.itfelix.liuhaizhuaichat.annotation.DistributedLock;
import com.itfelix.liuhaizhuaichat.utils.DistributedLockUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 分布式锁切面
 * @author aceFelix
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final DistributedLockUtil distributedLockUtil;
    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String lockKey = parseLockKey(joinPoint, distributedLock.key());
        
        boolean locked = distributedLockUtil.tryLock(lockKey, distributedLock.waitTime(), distributedLock.leaseTime());
        
        if (!locked) {
            log.warn("获取分布式锁失败: key={}, message={}", lockKey, distributedLock.message());
            throw new RuntimeException(distributedLock.message());
        }
        
        try {
            log.debug("获取分布式锁成功，开始执行方法: key={}", lockKey);
            return joinPoint.proceed();
        } finally {
            distributedLockUtil.unlock(lockKey);
            log.debug("释放分布式锁: key={}", lockKey);
        }
    }

    /**
     * 解析SpEL表达式获取锁的key
     */
    private String parseLockKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = nameDiscoverer.getParameterNames(method);
        
        EvaluationContext context = new StandardEvaluationContext();
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }
        
        Expression expression = parser.parseExpression(keyExpression);
        return expression.getValue(context, String.class);
    }
}
