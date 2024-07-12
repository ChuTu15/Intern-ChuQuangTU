package com.example.springboot.demo.spring.boot.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface LoggingService {

    void logRequestAndResponse(Map<String, Object> logData);
}
