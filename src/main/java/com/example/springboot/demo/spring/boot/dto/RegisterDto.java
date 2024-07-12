package com.example.springboot.demo.spring.boot.dto;

import com.example.springboot.demo.spring.boot.model.Role;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterDto implements Serializable {

    //it's a Data Trasfer Object for registration
    String firstName ;
    String lastName ;
    String email;
    String password ;
}
