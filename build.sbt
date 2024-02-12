version := "1.0.0"
name := "gatling-kafka"
scalaVersion := "2.13.12"
libraryDependencies ++= Seq(
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.9.5",
  "io.gatling" % "gatling-test-framework" % "3.9.5",
  "org.apache.kafka" % "kafka-clients" % "7.5.1-ccs",
)

resolvers += "confluent" at "https://packages.confluent.io/maven/"
