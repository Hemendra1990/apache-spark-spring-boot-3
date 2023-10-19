package com.hemendra.sparkdemo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "product")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private String description;
    private Double price;

    private String brand;
    private String seller;
    private String sellerAddress;
    private String sellerContact;
    private String sellerEmail;
    private String sellerWebsite;
    private Integer sellerRating;
    private String sellerReview;
    private Integer sellerReviewCount;

    


    
    
}
