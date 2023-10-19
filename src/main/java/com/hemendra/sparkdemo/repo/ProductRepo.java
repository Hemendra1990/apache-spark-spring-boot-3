package com.hemendra.sparkdemo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hemendra.sparkdemo.entity.Product;


@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    
}
