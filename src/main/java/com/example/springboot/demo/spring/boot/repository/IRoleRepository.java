package com.example.springboot.demo.spring.boot.repository;

import com.example.springboot.demo.spring.boot.model.Role;
import com.example.springboot.demo.spring.boot.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoleRepository extends JpaRepository<Role,Integer> {

    Role findByRoleName(RoleName roleName);


}
