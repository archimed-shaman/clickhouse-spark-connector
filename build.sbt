

name := """clickhouse-spark-connector"""
organization := "com.semrush"
version := "1.3.0"

scalaVersion := "2.11.7"

//credentials += Credentials("jFrog", "10.2.95.5", "admin", "password")

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.0.0" % "provided",
  "org.apache.spark" % "spark-sql_2.11" % "2.0.0" % "provided",
  "ru.yandex.clickhouse" % "clickhouse-jdbc" % "0.1.21",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.6.5"
)

fork in run := true

test in assembly := {}

assemblyMergeStrategy in assembly := {
  case n if n.startsWith("META-INF/MANIFEST.MF") => MergeStrategy.discard
  case "reference.conf"                          => MergeStrategy.concat
  case x => MergeStrategy.first
}

packAutoSettings
