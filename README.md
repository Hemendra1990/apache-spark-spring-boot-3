
// Create SparkSession
SparkSession spark = SparkSession.builder()
        .appName("complex query")
        .getOrCreate();

// Read tables using Spark JDBC data source 
Dataset<Row> tableA = spark.read()
        .format("jdbc")
        .option("url", jdbcURL) 
        .option("dbtable", "tableA")
        .load();

Dataset<Row> tableB = spark.read()
        .format("jdbc")
        .option("url", jdbcURL)
        .option("dbtable", "tableB")
        .load(); 
        
Dataset<Row> tableC = spark.read()
        .format("jdbc")
        .option("url", jdbcURL)
        .option("dbtable", "tableC")
        .load();

// Register as temporary views       
tableA.createOrReplaceTempView("tableA");
tableB.createOrReplaceTempView("tableB");
tableC.createOrReplaceTempView("tableC");

// Run query using SparkSession.sql()
Dataset<Row> result = spark.sql("SELECT * FROM tableA a JOIN tableB b ON a.id = b.a_id JOIN tableC c ON a.id = c.a_id WHERE a.date > CURRENT_DATE - INTERVAL 30 DAYS");

// Persist result to disk
result.persist(StorageLevel.DISK_ONLY()); 

// In later job, retrieve cached data
Dataset<Row> cachedResult = spark.read()
        .format("parquet")
        .load("/cached/data");