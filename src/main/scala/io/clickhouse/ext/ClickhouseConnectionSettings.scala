package io.clickhouse.ext

case class ClickhouseConnectionSettings(
  host: String,
  port: Int = 8123,
  cluster: Option[String] = None,
  user: Option[String] = None,
  password: Option[String] = None,
  db: String,
  table: String,
  props: Seq[(String, String)] = Seq()
)
