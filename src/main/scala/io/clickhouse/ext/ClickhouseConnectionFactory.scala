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

    settings.props map {
      case (k, v) => props.setProperty(k, v)
    }

    val clickHouseProps = new ClickHouseProperties(props) withCredentials (settings.user.getOrElse(""), settings.password.getOrElse(""))
    new ClickHouseDataSource(s"jdbc:clickhouse://${settings.host}:${settings.port}", clickHouseProps)
  }
}
