package com.itfelix.liuhaizhuaichat.aspect;

import com.itfelix.liuhaizhuaichat.annotation.RateLimit;
import com.itfelix.liuhaizhuaichat.utils.RateLimiterUtil;
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
 * 请求限流切面
 * @author aceFelix
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimiterUtil rateLimiterUtil;
    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String limitKey = parseLimitKey(joinPoint, rateLimit.key());
        
        boolean allowed = false;
        
        switch (rateLimit.type()) {
            case TOKEN_BUCKET:
                allowed = rateLimiterUtil.tryAcquire(limitKey, rateLimit.capacity(), rateLimit.rate());
                break;
            case SLIDING_WINDOW:
                allowed = rateLimiterUtil.tryAcquireWithWindow(limitKey, rateLimit.capacity(), rateLimit.rate());
                break;
            case FIXED_WINDOW:
                allowed = rateLimiterUtil.tryAcquireWithFixedWindow(limitKey, rateLimit.capacity(), rateLimit.rate());
                break;
        }
        
        if (!allowed) {
            log.warn("请求被限流: key={}, type={}, capacity={}, rate={}", 
                    limitKey, rateLimit.type(), rateLimit.capacity(), rateLimit.rate());
            throw new RuntimeException(rateLimit.message());
        }
        
        return joinPoint.proceed();
    }

    /**
     * 解析SpEL表达式获取限流key
     */
    private String parseLimitKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        if (keyExpression == null || keyExpression.isEmpty()) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            return signature.getMethod().getName();
        }
        
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
