package com.example.springboot.demo.spring.boot.service;

import com.example.springboot.demo.spring.boot.dto.LogDto;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Service
public class LokiService {

    @Value("${loki.url}")
    private String lokiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    public LokiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String getLogs(String query) {

        String url = "http://localhost:3100/loki/api/v1/query?query={query}";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class, query);
        return getString(response);

    }

    public String getLogError(long time) {
        Instant now = Instant.now();
        long endTime = now.toEpochMilli() * 1_000_000;
        long startTime = now.minusSeconds(time*60).toEpochMilli() * 1_000_000;

        System.out.println("Start Time: " + startTime);
        System.out.println("End Time: " + endTime);

        String query = "{path=\"/api/auth/login\", level=\"INFO\", status!=\"200\"} != `\"status\": \"\"`";

        String url = "http://localhost:3100/loki/api/v1/query_range?query={query}&start={startTime}&end={endTime}";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class, query, startTime, endTime);
        return getString(response);
    }

    private String getString(ResponseEntity<Map> response) {
        Map<String, Object> responseBody = response.getBody();

        if (responseBody != null && responseBody.containsKey("data")) {
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data.containsKey("result")) {
                List<Object> result = (List<Object>) data.get("result");


                try {
                    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result).replace("\\n", "\n").replace("\\","");
                } catch (Exception e) {
                    throw new RuntimeException("Failed to convert result to JSON string", e);
                }
            }
        }
        return "{}";
    }


}


