package com.liuencier.component.ratelimiter.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流核心注解
 *
 * @author liuenci
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 重复点击次数，默认1
     *
     * @return
     */
    int time() default 1;

    /**
     * 过期时间，默认5s
     *
     * @return
     */
    long expireTime() default 5;

    /**
     * 过期时间单位，默认秒
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
