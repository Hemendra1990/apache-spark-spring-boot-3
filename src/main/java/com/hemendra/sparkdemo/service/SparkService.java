package com.hemendra.sparkdemo.service;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.storage.StorageLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SparkService {
    // Map to cache DataFrames per query
    private String cacheDir;

    private final SparkSession sparkSession;

    @Autowired
    public SparkService(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
        this.cacheDir = "cache";
    }

    public void readAllPostgresTables(String jdbcUrl, String username, String password) {
        // Establish a JDBC connection to the PostgreSQL database
        Dataset<Row> allTables = sparkSession.read()
                .jdbc(jdbcUrl, "information_schema.tables", getJDBCProperties(username, password));

        // Filter to retrieve only the user tables
        Dataset<Row> userTables = allTables.filter("table_schema = 'public'");

        log.info("Found {} user tables", userTables.count());

        // Loop through the user tables and read their data
        userTables.select("table_name").collectAsList().forEach(row -> {
            String tableName = row.getString(0);
            Dataset<Row> tableData = sparkSession.read()
                    .jdbc(jdbcUrl, "public." + tableName, getJDBCProperties(username, password));

            // Process or save the tableData as needed
            tableData.show();
        });
    }

    private java.util.Properties getJDBCProperties(String username, String password) {
        java.util.Properties properties = new java.util.Properties();
        properties.setProperty("user", username);
        properties.setProperty("password", password);
        return properties;
    }

    // generate a method which will take sql query from the user and execute it
    // using the spark session and return the result
    public Dataset<Row> executeQuery(String query) {
        return sparkSession.sql(query);
    }

    // generate a method which will take sql query from the user and execute it
    // using the spark session and return the result and cache it
    public Dataset<Row> executeQueryAndCache(String query) {

        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Query cannot be null or empty");
        }

        if (isCached(query)) {
            log.info("Reading from cache");
            Dataset<Row> cachedRow = readCache(query);
            cachedRow.createOrReplaceTempView("student");

            // Testing with Temp view
            sparkSession.sql(
             "select * from student limit 10"   
            ).collectAsList().forEach(row -> {
                log.info("Row: {}", row);
            });

            return cachedRow;
        }

        // configure sparksession with jdbc properties
        sparkSession.conf().set("spark.sql.autoBroadcastJoinThreshold", -1);
        sparkSession.conf().set("spark.sql.shuffle.partitions", 1000);

        // set database properties with sparksession
        Properties properties = new Properties();
        properties.put("user", "postgres");
        properties.put("password", "postgres");
        properties.put("driver", "org.postgresql.Driver");
        properties.put("fetchsize", "10000");
        properties.put("dbtable", "public.student");//(query) as table_name");
        properties.put("schema", "public");

        Dataset<Row> studentDataset = sparkSession.read().jdbc("jdbc:postgresql://localhost:5432/employee", "public.student", properties);
        studentDataset.createOrReplaceTempView("student");

        // Execute the query and cache the result
        studentDataset.persist(StorageLevel.DISK_ONLY());
        studentDataset.write().format("parquet").save(cacheDir + "/" + "student");

        return studentDataset;
    }

    private boolean isCached(String query) {
        // Check if data file exists in cache directory
        return new File(cacheDir + "/" + "student").exists();
    }

    private Dataset<Row> readCache(String query) {
        // Read cached data from disk
        return sparkSession.read()
                .format("parquet")
                .load(cacheDir + "/" + "student");
    }
}
