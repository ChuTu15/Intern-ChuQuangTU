package com.example.springboot.demo.spring.boot.controller;

import com.example.springboot.demo.spring.boot.service.LokiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LokiController {

    @Autowired
    private LokiService lokiService;

    @GetMapping("/logs/")
    public String getLogs(@RequestParam String query) {
        return lokiService.getLogs(query);
    }

    @GetMapping("/logs/error")
    public String getLogError(@RequestParam long time) {
        return lokiService.getLogError(time);

    }
}

//import com.example.springboot.demo.spring.boot.service.LokiService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;
//
//@RestController
//public class LokiController {
//
//    @Autowired
//    private LokiService lokiService;
//
//    @GetMapping("/logs")
//    public Mono<String> getLogs(@RequestParam String query) {
//        return lokiService.queryLogs(query);
//    }
//}
