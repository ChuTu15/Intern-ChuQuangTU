package com.example.springboot.demo.spring.boot.service;

import com.example.springboot.demo.spring.boot.model.Student;
import com.example.springboot.demo.spring.boot.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public abstract class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentByName(String name) {
        return studentRepository.findByName(name).orElse(null);
    }

    public Student saveStudent(Student student) {
        studentRepository.save(student);
        return student;
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public List<Student> getStudentByCourse(String course) {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .filter(student -> course.equals(student.getCourse()))
                .collect(Collectors.toList());
    }

    public Student getStudentByStudentId(Long studentId) {
        return studentRepository.findByStudentId(studentId).orElse(null);
    }
}
