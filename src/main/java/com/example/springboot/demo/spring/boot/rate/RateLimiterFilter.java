package com.example.springboot.demo.spring.boot.rate;

import io.github.resilience4j.ratelimiter.RateLimiter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Slf4j
public class RateLimiterFilter extends OncePerRequestFilter {

    @Autowired
    private final RateLimiterFactory rateLimiterFactory;

    @Autowired
    public RateLimiterFilter(RateLimiterFactory rateLimiterFactory) {
        this.rateLimiterFactory = rateLimiterFactory;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        HttpServletRequest httpRequest = request;
        HttpServletResponse httpResponse = response;
        String ipAddress = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        String requestURI = httpRequest.getRequestURI();
        String apiType;

        if (requestURI.startsWith("/api/user")) {
            apiType = "userApi";
        } else if (requestURI.startsWith("/api/admin")) {
            apiType = "adminApi";
        } else {
            filterChain.doFilter(request,response);
            return;
        }

        RateLimiter rateLimiter = rateLimiterFactory.getRateLimiter(ipAddress, apiType);
        log.info("Rate limiter: " + rateLimiter.toString());

        boolean permissionGranted = rateLimiter.acquirePermission();

        if (permissionGranted) {
            log.info("Request from IP {} to URI {} allowed", ipAddress, requestURI);
            filterChain.doFilter(request,response);
        } else {
            log.info("Rate limit exceeded. Request from IP {} to URI {} denied", ipAddress, requestURI);
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.getWriter().write("Rate limit exceeded. Please try again later.");
        }
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}
