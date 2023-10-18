package com.hemendra.sparkdemo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hemendra.sparkdemo.entity.Employee;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    

}
