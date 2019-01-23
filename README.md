clickhouse spark connector
==========================

> connector #spark DataFrame -> Yandex #ClickHouse table
 
Example
``` scala

    import io.clickhouse.ext.ClickhouseConnectionFactory
    import io.clickhouse.ext.ClickhouseConnectionSettings
    import io.clickhouse.ext.spark.ClickhouseSparkExt._
    import org.apache.spark.sql.SparkSession

    // spark config
    val sparkSession = SparkSession.builder
      .master("local")
      .appName("local spark")
      .getOrCreate()

    val sc = sparkSession.sparkContext
    val sqlContext = sparkSession.sqlContext
    
    // create test DF
    case class Row1(name: String, v: Int, v2: Int)
    val df = sqlContext.createDataFrame(1 to 1000 map(i => Row1(s"$i", i, i + 10)) )

    // clickhouse params
    
    // any node 
    val anyHost = "localhost"
    val db = "tmp1"
    val tableName = "t1"
    // cluster configuration must be defined in config.xml (clickhouse config)
    val clusterName = Some("perftest_1shards_1replicas"): Option[String]
    
    val chSettings = ClickhouseConnectionSettings(
      host = anyHost,
      cluster = clusterName,
      password = Some("secret_password"),
      db = db,
      table = tableName,
      props = Seq(
        ("max_memory_usage", "100000000000"),
        ("socket_timeout", "1000000"),
        ("max_threads", "8"),
        ("max_bytes_before_external_group_by", "80000000000"),
        ("max_bytes_before_external_sort", "80000000000")
      )
    )

    // define clickhouse datasource
    implicit val clickhouseDataSource = ClickhouseConnectionFactory.get(chSettings)
    
    // create db / table
    //df.dropClickhouseDb(chSettings)
    df.createClickhouseDb(chSettings)
    df.createClickhouseTable(chSettings, "mock_date", Seq("name"))

    // save DF to clickhouse table
    val res = df.saveToClickhouse(chSettings, (row) => java.sql.Date.valueOf("2000-12-01"), "mock_date", batchSize = 100000, sleep = 1000)
    assert(res.size == 1)
    assert(res.get("localhost") == Some(df.count()))

```

To build and run:
 * git clone
 * sbt assembly
 * sbt publishLocal

