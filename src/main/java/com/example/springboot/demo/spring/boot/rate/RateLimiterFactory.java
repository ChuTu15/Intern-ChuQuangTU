package com.example.springboot.demo.spring.boot.rate;

import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.stereotype.Component;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterFactory {

    private final Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();
    private final RateLimiterRegistry rateLimiterRegistry;

    @Value("${resilience4j.ratelimiter.instances.userApi.limitForPeriod}")
    private int userApiLimitForPeriod;
    @Value("${resilience4j.ratelimiter.instances.userApi.limitRefreshPeriod}")
    private String userApiLimitRefreshPeriod;
    @Value("${resilience4j.ratelimiter.instances.userApi.timeoutDuration}")
    private String userApiTimeoutDuration;

    @Value("${resilience4j.ratelimiter.instances.adminApi.limitForPeriod}")
    private int adminApiLimitForPeriod;
    @Value("${resilience4j.ratelimiter.instances.adminApi.limitRefreshPeriod}")
    private String adminApiLimitRefreshPeriod;
    @Value("${resilience4j.ratelimiter.instances.adminApi.timeoutDuration}")
    private String adminApiTimeoutDuration;

    public RateLimiterFactory(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    public RateLimiter getRateLimiter(String key, String apiType) {
        return rateLimiterMap.computeIfAbsent(key + "-" + apiType, k -> {
            RateLimiterConfig config;
            if ("userApi".equals(apiType)) {
                config = RateLimiterConfig.custom()
                        .limitForPeriod(userApiLimitForPeriod)
                        .limitRefreshPeriod(Duration.parse(userApiLimitRefreshPeriod))
                        .timeoutDuration(Duration.parse(userApiTimeoutDuration))
                        .build();
            } else {
                config = RateLimiterConfig.custom()
                        .limitForPeriod(adminApiLimitForPeriod)
                        .limitRefreshPeriod(Duration.parse(adminApiLimitRefreshPeriod))
                        .timeoutDuration(Duration.parse(adminApiTimeoutDuration))
                        .build();
            }
            return rateLimiterRegistry.rateLimiter(k, config);
        });
    }
}
