package com.liuencier.component.ratelimiter.constant;

/**
 * @program: rate-limiter
 * @description: 限流组件常量类
 * @author: liuenci
 * @create: 2022-01-23 22:14
 **/
public class RateLimiterConstant {

    /**
     * Redis相关类
     */
    public static final String PREFIX = "RATE_LIMITER_";

    /**
     * 异常消息
     */
    public static final String REPEAT_CLICK_MESSAGE = "请求频繁，请{}{}后重试";
}
