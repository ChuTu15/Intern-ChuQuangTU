package com.example.springboot.demo.spring.boot.rate;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RateLimiterFilter> rateLimiterFilterRegistration(RateLimiterFactory rateLimiterFactory) {
        FilterRegistrationBean<RateLimiterFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimiterFilter(rateLimiterFactory));
        registrationBean.addUrlPatterns("/api/user/*", "/api/admin/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
