package com.example.springboot.demo.spring.boot.controller;

import com.example.springboot.demo.spring.boot.model.Student;
import com.example.springboot.demo.spring.boot.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/user")
@Tag(name = "User Controller", description = "Operations pertaining to users in the system")
public class UserController {

    @Autowired
    private StudentService studentService;

    @Operation(summary = "List all students with user")
    @GetMapping("/students")
    public ResponseEntity<List<Student>> listStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "Get student by name")
    @GetMapping("/students/getname/{name}")
    public ResponseEntity<Student> getStudentByName(@PathVariable String name) {
        Student student = studentService.getStudentByName(name);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @Operation(summary = "Get student by id")
    @GetMapping("/students/getid/{studentId}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long studentId) {
        Student student = studentService.getStudentByStudentId(studentId);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @Operation(summary = "Get student by course")
    @GetMapping("/students/getcourse/{course}")
    public ResponseEntity<List<Student>> getStudentByCourse(@PathVariable String course) {
        List<Student> students = studentService.getStudentByCourse(course);
        if (students.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(students);
    }

}
