package com.example.springboot.demo.spring.boot.service;

import com.example.springboot.demo.spring.boot.dto.LoginDto;
import com.example.springboot.demo.spring.boot.dto.RegisterDto;
import com.example.springboot.demo.spring.boot.model.Role;
import com.example.springboot.demo.spring.boot.model.User;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    //ResponseEntity<?> register (RegisterDto registerDto);
    //  ResponseEntity<BearerToken> authenticate(LoginDto loginDto);

//    String authenticate(LoginDto loginDto);
//    ResponseEntity<?> register (RegisterDto registerDto);
    Role saveRole(Role role);

    User saverUser (User user) ;
}
