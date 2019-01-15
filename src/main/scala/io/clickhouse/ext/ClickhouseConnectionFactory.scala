package io.clickhouse.ext

import java.util.Properties
import ru.yandex.clickhouse.ClickHouseDataSource
import ru.yandex.clickhouse.settings.ClickHouseProperties

object ClickhouseConnectionFactory extends Serializable {

  private val dataSources = scala.collection.mutable.Map[(String, Int), ClickHouseDataSource]()

  def get(settings: ClickhouseConnectionSettings): ClickHouseDataSource = {
    val host = settings.host
    val port = settings.port
    dataSources.get((host, port)) match {
      case Some(ds) =>
        ds
      case None =>
        val ds = createDatasource(settings)
        dataSources += ((host, port) -> ds)
        ds
    }
  }

  private def createDatasource(settings: ClickhouseConnectionSettings) = {
    val props = new Properties()

    props.setProperty("database", settings.db)

    settings.user map { user => props.setProperty("user", user) }
    settings.password map { pass => props.setProperty("password", pass) }

    settings.props map {
      case (k, v) => props.setProperty(k, v)
    }
    // props.setProperty("max_memory_usage", "100000000000")
    // props.setProperty("socket_timeout", "1000000")
    // props.setProperty("max_threads", "8")
    // props.setProperty("max_bytes_before_external_group_by", "80000000000")
    // props.setProperty("max_bytes_before_external_sort", "80000000000")

    val clickHouseProps = new ClickHouseProperties(props)
    new ClickHouseDataSource(s"jdbc:clickhouse://${settings.host}:${settings.port}", clickHouseProps)
  }
}
