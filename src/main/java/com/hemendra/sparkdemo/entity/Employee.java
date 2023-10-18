package com.hemendra.sparkdemo.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "employee") // This is the name of the table in the database
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long eid;
    private String ename;
    private String email;
    private String address;
    private Integer age;
    private String designation;
    private Double salary;
    private String department;
    private String country;
    private String city;
    private String state;
    private String zipcode;
    private String phone;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String hireDate;
    private String terminationDate;
    private String supervisorName;
    private String supervisorEmail;
    private String supervisorPhone;
    private String jobTitle;

}
