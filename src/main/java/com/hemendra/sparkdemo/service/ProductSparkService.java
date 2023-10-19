package com.hemendra.sparkdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.storage.StorageLevel;

import com.github.javafaker.Faker;
import com.hemendra.sparkdemo.entity.Product;
import com.hemendra.sparkdemo.repo.ProductRepo;

import jakarta.annotation.PostConstruct;

@Service
public class ProductSparkService {

    @Value("${spark.cache.dir:spark-cache}")
    private String sparkCacheDir;

    private ProductRepo productRepo;
    private SparkSession sparkSession;

    @Autowired
    public ProductSparkService(ProductRepo productRepo, SparkSession sparkSession) {
        this.productRepo = productRepo;
        this.sparkSession = sparkSession;
    }

    @PostConstruct
    public void init() {
        initialise1000Products();
    }

    /**
     * Use Once store it in database as well as Spark Cache
     */
    public void initialise1000Products() {
        Faker faker = new Faker(new Locale("en-US"));
        List<Product> products = new ArrayList<>();
        for(int i=0; i<10; i++) {
            Product product = new Product();
            product.setName(faker.commerce().productName());
            product.setDescription(faker.lorem().sentence());
            product.setPrice(faker.number().randomDouble(2, 10, 1000));

            product.setCategory(faker.commerce().department());
            product.setBrand(faker.company().name());
            product.setSeller(faker.company().name());
            product.setSellerAddress(faker.address().fullAddress());
            product.setSellerContact(faker.phoneNumber().phoneNumber());
            product.setSellerEmail(faker.internet().emailAddress());
            product.setSellerWebsite(faker.internet().url());
            product.setSellerRating(faker.number().numberBetween(1, 5));
            product.setSellerReview(faker.lorem().sentence());
            product.setSellerReviewCount(faker.number().numberBetween(1, 1000));
            

            products.add(product);
            
        }
        try {
            productRepo.saveAll(products);

            Dataset<Row> createDataFrame = sparkSession.createDataFrame(products, Product.class);
            createDataFrame.persist(StorageLevel.DISK_ONLY());
            createDataFrame.write().mode("append").format("parquet").save(sparkCacheDir+"/product");
            createDataFrame.show();
            createDataFrame.cache().createOrReplaceTempView("product");

        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }

    public void saveProduct(Product product) {
        Product savedProduct = productRepo.save(product);
        Dataset<Row> newProduct = sparkSession.createDataFrame(List.of(savedProduct), Product.class);

        sparkSession.read()
                .parquet(sparkCacheDir+"/product")
                .union(newProduct)
                .dropDuplicates()
                .write()
                .mode("overwrite")
                .save(sparkCacheDir+"/product");
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        sparkSession.read().parquet(sparkCacheDir+"/product").createOrReplaceTempView("product");
        sparkSession.sql("select * from product").collectAsList().stream().forEach(pRow -> {

            //Convert Row to Product
            Product product = new Product();
            product.setId(pRow.getAs("id"));
            product.setName(pRow.getAs("name"));
            product.setCategory(pRow.getAs("category"));
            product.setDescription(pRow.getAs("description"));
            product.setPrice(pRow.getAs("price"));
            product.setBrand(pRow.getAs("brand"));
            product.setSeller(pRow.getAs("seller"));
            product.setSellerAddress(pRow.getAs("sellerAddress"));
            product.setSellerContact(pRow.getAs("sellerContact"));
            product.setSellerEmail(pRow.getAs("sellerEmail"));
            product.setSellerWebsite(pRow.getAs("sellerWebsite"));
            product.setSellerRating(pRow.getAs("sellerRating"));
            product.setSellerReview(pRow.getAs("sellerReview"));
            product.setSellerReviewCount(pRow.getAs("sellerReviewCount"));
            products.add(product);
        });

        return products;
    }
}
