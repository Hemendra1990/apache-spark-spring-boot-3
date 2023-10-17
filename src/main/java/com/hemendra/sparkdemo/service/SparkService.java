package com.hemendra.sparkdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SparkService {
    private final SparkSession sparkSession;

    @Autowired
    public SparkService(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
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

    // generate a method which will take sql query from the user and execute it using the spark session and return the result

    // generate a method which will take sql query from the user and execute it using the spark session and return the result

    public Dataset<Row> executeQuery(String query) {
        return sparkSession.sql(query);
    }
}
