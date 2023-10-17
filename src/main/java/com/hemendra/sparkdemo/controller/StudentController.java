package com.hemendra.sparkdemo.controller;

import com.hemendra.sparkdemo.entity.Student;
import com.hemendra.sparkdemo.repo.StudentRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/student")
@Slf4j
public class StudentController {
    private StudentRepo studentRepo;

    @Autowired
    public StudentController(StudentRepo studentRepo) {
        this.studentRepo = studentRepo;
    }

    @GetMapping
    public Object getAllStudents() {
        return studentRepo.findAll();
    }

    @PostMapping
    public Student saveStudent(@RequestBody Student student) {
        return studentRepo.save(student);
    }

    

    @GetMapping("/insert")
    public void insert1MillionRecord() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100000; i += 1000) {
            executorService.execute(() -> {
                List<Student> students = new ArrayList<>();
                Faker faker = new Faker();
                for (int j = 0; j < 1000; j++) {
                    Student student = new Student();
                    student.setName(faker.name().fullName());
                    student.setAge(faker.number().numberBetween(18, 30));
                    student.setAddress(faker.address().fullAddress());
                    students.add(student);
                }
                studentRepo.saveAll(students);
                log.info("Saved 1000 records iteration: {}");
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
