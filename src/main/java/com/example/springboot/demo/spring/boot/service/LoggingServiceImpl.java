package com.example.springboot.demo.spring.boot.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class LoggingServiceImpl implements LoggingService {

    @Override
    public void logRequestAndResponse(Map<String, Object> logData) {
        HttpServletRequest request = (HttpServletRequest) logData.get("request");
        HttpServletResponse response = (HttpServletResponse) logData.get("response");
        Object requestBody = logData.get("requestBody");
        Object responseBody = logData.get("responseBody");

        if (request == null || response == null) {
            return;
        }

        // Set MDC values for request and response
        MDC.put("method", request.getMethod());
        MDC.put("path", request.getRequestURI());
        MDC.put("headers", buildHeadersMap(request).toString());
        MDC.put("responseHeaders", buildHeadersMap(response).toString());
        MDC.put("status", String.valueOf(response.getStatus()));

        if (requestBody != null) {
            MDC.put("requestBody", requestBody.toString());
        }

        if (responseBody != null) {
            MDC.put("responseBody", responseBody.toString());
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("REQUEST AND RESPONSE");

        log.info(stringBuilder.toString());

        // Clear MDC after logging
        MDC.clear();
    }

    private Map<String, String> buildParametersMap(HttpServletRequest httpServletRequest) {
        Map<String, String> resultMap = new HashMap<>();
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String value = httpServletRequest.getParameter(key);
            resultMap.put(key, value);
        }

        return resultMap;
    }

    private Map<String, String> buildHeadersMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }

    private Map<String, String> buildHeadersMap(HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();

        Collection<String> headerNames = response.getHeaderNames();
        for (String header : headerNames) {
            map.put(header, response.getHeader(header));
        }

        return map;
    }
}
