package com.example.springboot.demo.spring.boot.controller;

import com.example.springboot.demo.spring.boot.model.Role;
import com.example.springboot.demo.spring.boot.model.RoleName;
import com.example.springboot.demo.spring.boot.model.Student;
import com.example.springboot.demo.spring.boot.model.User;
import com.example.springboot.demo.spring.boot.repository.IRoleRepository;
import com.example.springboot.demo.spring.boot.repository.IUserRepository;
import com.example.springboot.demo.spring.boot.service.StudentService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Controller", description = "Operations pertaining to admins in the system")
public class AdminController {

    public AdminController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Autowired
    private StudentService studentService;

    @Autowired
    private IUserRepository IUserService;

    @Autowired
    private IRoleRepository IRoleService;

    @Operation(summary = "List all students with admin")
    @GetMapping("/students")
    public ResponseEntity<List<Student>> listStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    private ResponseEntity<List<Student>> getFallbackMethod(Exception e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @Operation(summary = "Get student by name")
    @GetMapping("/students/getname/{name}")
    public ResponseEntity<Student> getStudentByName(@PathVariable String name) {
        Student student = studentService.getStudentByName(name);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        log.info("Admin get student by name");
        return ResponseEntity.ok(student);
    }

    @Operation(summary = "Get student by id")
    @GetMapping("/students/getid/{studentId}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long studentId) {
        Student student = studentService.getStudentByStudentId(studentId);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        log.info("Admin get student by id");
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

    @GetMapping("/users")
    @Operation(summary = "List all users")
    public ResponseEntity<List<User>> listUsers() {
        List<User> users = IUserService.findAll();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/delete/{userId}")
    @Operation(summary = "Delete user")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userId) {
        Optional<User> userOptional = IUserService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();
        IUserService.delete(user);

        return ResponseEntity.ok("User deleted successfully");
    }



    @PutMapping("/upgrade/{userId}")
    @Operation(summary = "Upgrade user to admin")
    public ResponseEntity<String> upgradeUserToAdmin(@PathVariable Integer userId) {
        Optional<User> userOptional = IUserService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();
        Role adminRole = IRoleService.findByRoleName(RoleName.ADMIN);
        Role userRole = IRoleService.findByRoleName(RoleName.USER);
        if (adminRole == null) {
            return ResponseEntity.status(500).body("Admin role not found");
        }

        user.getRoles().remove(userRole);
        user.getRoles().add(adminRole);
        IUserService.save(user);

        return ResponseEntity.ok("User upgraded to admin successfully");
    }

    @Operation(summary = "Edit student")
    @PutMapping("/students/edit/{id}")
    public ResponseEntity<Student> editStudent(@PathVariable Long id, @RequestBody Student student) {
        Optional<Student> existingStudentOptional = Optional.ofNullable(studentService.getStudentById(id));
        if (existingStudentOptional.isPresent()) {
            Student existingStudent = existingStudentOptional.get();

            // Update the existing student's details with the new values
            existingStudent.setName(student.getName());
            existingStudent.setCourse(student.getCourse());
            existingStudent.setEmail(student.getEmail());
            // Add any other fields that need to be updated

            // Save the updated student
            Student updatedStudent = studentService.saveStudent(existingStudent);
            log.info("Admin edit student");
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Add student")
    @PostMapping("/students/add")
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        Student savedStudent = studentService.saveStudent(student);
        log.info("Admin add student");
        return ResponseEntity.ok(savedStudent);
    }

    @Operation(summary = "Delete student")
    @DeleteMapping("/students/delete/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        log.info("Admin delete student");
        return ResponseEntity.ok("Student deleted successfully");
    }

}
