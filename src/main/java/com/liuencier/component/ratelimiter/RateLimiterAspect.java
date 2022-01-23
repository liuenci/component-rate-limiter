package com.liuencier.component.ratelimiter;

import com.liuencier.component.ratelimiter.annotation.RateLimiter;
import com.liuencier.component.ratelimiter.constant.RateLimiterConstant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static com.liuencier.component.ratelimiter.constant.RateLimiterConstant.PREFIX;

/**
 * @program: rate-limiter
 * @description: 限流切面
 * @author: liuenci
 * @create: 2022-01-23 21:50
 **/
@Aspect
@Component
public class RateLimiterAspect {

    @Value("${component.rate_limiter.enabled:false}")
    private boolean enabled;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.liuencier.component.ratelimiter.annotation.RateLimiter)")
    public void RateLimiter() {
    }

    /**
     * 环绕通知 （可以控制目标方法前中后期执行操作，目标方法执行前后分别执行一些代码）
     *
     * @param joinPoint
     * @return
     */
    @Before("RateLimiter()")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!enabled) {
            return joinPoint.proceed();
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        RateLimiter annotation = method.getAnnotation(RateLimiter.class);

        // TODO 从上下文拿到用户信息，添加到 Redis Key 中
        String redisKey = PREFIX.concat(method.getDeclaringClass().getName()).concat(method.getName());
        String redisValue = redisKey.concat(String.valueOf(annotation.time())).concat("repeat click");

        if (!redisTemplate.hasKey(redisKey)) {
            redisTemplate.opsForValue().set(redisKey, redisValue, annotation.expireTime(), annotation.timeUnit());
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                redisTemplate.delete(redisKey);
                throw new RuntimeException(throwable);
            }
        } else {
            String message = String.format(RateLimiterConstant.REPEAT_CLICK_MESSAGE, annotation.expireTime(), annotation.timeUnit());
            throw new RuntimeException(message);
        }
    }
}
