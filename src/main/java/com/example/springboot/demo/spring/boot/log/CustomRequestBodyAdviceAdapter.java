package com.example.springboot.demo.spring.boot.log;

import com.example.springboot.demo.spring.boot.service.LoggingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomRequestBodyAdviceAdapter extends RequestBodyAdviceAdapter {

    @Autowired
    LoggingService loggingService;

    private static final ThreadLocal<Map<String, Object>> logContext = ThreadLocal.withInitial(HashMap::new);

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
                                MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {

        HttpServletRequest request = ((ServletServerHttpRequest) inputMessage).getServletRequest();
        Map<String, Object> logData = logContext.get();
        logData.put("request", request);
        logData.put("requestBody", body);
        loggingService.logRequestAndResponse(logData);
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }
}
