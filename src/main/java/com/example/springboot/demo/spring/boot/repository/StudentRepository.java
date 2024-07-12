package com.example.springboot.demo.spring.boot.repository;

import com.example.springboot.demo.spring.boot.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByName(String name);

    List<Student> findByCourse(String course);

    Optional<Student> findByStudentId(Long studentId);
}