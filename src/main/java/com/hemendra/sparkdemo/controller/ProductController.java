package com.hemendra.sparkdemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hemendra.sparkdemo.entity.Product;
import com.hemendra.sparkdemo.service.ProductSparkService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController {

    @PostConstruct
    public void init() {
        log.info("ProductController init");
    }
    
    @Autowired
    private ProductSparkService productSparkService;

    @PostMapping
    public void insertProduct(@RequestBody Product product) {
        productSparkService.saveProduct(product);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        List<Product> allProducts = productSparkService.getAllProducts();
        return allProducts;
    }

}
