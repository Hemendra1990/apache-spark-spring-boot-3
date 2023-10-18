package com.hemendra.sparkdemo.controller;

import com.hemendra.sparkdemo.dto.DbPropertiesDto;
import com.hemendra.sparkdemo.service.SparkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/spark")
public class SparkController {

    private SparkService sparkService;

    @Autowired
    public SparkController(SparkService sparkService) {
        this.sparkService = sparkService;
    }

    @PostMapping
    public void readAllPostgresTables(@RequestBody DbPropertiesDto dbPropertiesDto) {
        /*sparkService.readAllPostgresTables(
            "jdbc:postgresql://localhost:5432/postgres",
            "postgres",
            "postgres"
        );*/
        sparkService.readAllPostgresTables(
            dbPropertiesDto.getUrl(),
            dbPropertiesDto.getUsername(),
            dbPropertiesDto.getPassword()
        );
        log.info("Done!");
    }

    @GetMapping("/query")
    public Object executeQuery(@RequestParam String sql) {
        return sparkService.executeQueryAndCache(sql).collectAsList().stream().count();
    }
}
