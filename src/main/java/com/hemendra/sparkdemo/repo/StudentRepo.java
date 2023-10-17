package com.hemendra.sparkdemo.repo;

import com.hemendra.sparkdemo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepo extends JpaRepository<Student, Integer> {
}
