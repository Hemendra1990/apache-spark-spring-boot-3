package com.hemendra.sparkdemo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.javafaker.Faker;
import com.hemendra.sparkdemo.entity.Employee;
import com.hemendra.sparkdemo.repo.EmployeeRepo;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeRepo employeeRepository;

    @PostMapping("/insert-batch")
    public String insertBatch() {
        int batchSize = 10000;
        int totalRecords = 1000000;
        int batches = totalRecords / batchSize;

        Faker faker = new Faker();

        for (int i = 0; i < batches; i++) {
            List<Employee> employees = new ArrayList<>();
            for (int j = 0; j < batchSize; j++) {
                log.info("Inserting record {} of {}", i * batchSize + j, totalRecords);
                Employee employee = new Employee();
                employee.setEname(faker.name().fullName());
                employee.setEmail(faker.internet().emailAddress());
                employee.setAddress(faker.address().fullAddress());
                employee.setAge(faker.number().numberBetween(18, 60));
                employee.setDesignation(faker.job().title());
                employee.setSalary(faker.number().randomDouble(2, 10000, 100000));
                employee.setDepartment(faker.job().field());
                employee.setCountry(faker.address().country());
                employee.setCity(faker.address().city());
                employee.setState(faker.address().state());
                employee.setZipcode(faker.address().zipCode());
                employee.setPhone(faker.phoneNumber().phoneNumber());
                employee.setEmergencyContactName(faker.name().fullName());
                employee.setEmergencyContactPhone(faker.phoneNumber().phoneNumber());
                employee.setHireDate(faker.date().birthday().toString());
                employee.setTerminationDate(faker.date().birthday().toString());
                employee.setSupervisorName(faker.name().fullName());
                employee.setSupervisorEmail(faker.internet().emailAddress());
                employee.setSupervisorPhone(faker.phoneNumber().phoneNumber());
                employee.setJobTitle(faker.job().title());
                employees.add(employee);
            }
            employeeRepository.saveAll(employees);
        }

        return "insert-batch";
    }
    
}
