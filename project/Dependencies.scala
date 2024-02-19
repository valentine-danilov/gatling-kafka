import sbt._

object Dependencies {
  private val gatling = "io.gatling" % "gatling-test-framework" % "3.9.5"
  private val gatlingHighcharts = "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.9.5"
  private val kafkaClients = "org.apache.kafka" % "kafka-clients" % "7.5.1-ccs"
  private val avro = "org.apache.avro" % "avro" % "1.11.3"
  private val avroSerializer = "io.confluent" % "kafka-avro-serializer" % "7.5.1"
  private val spotbugs = "com.github.spotbugs" % "spotbugs-annotations" % "4.8.3"
  
  val kafkaDependencies = Seq(
    gatling,
    gatlingHighcharts,
    avro,
    avroSerializer
  )
  
  val kafkaJavaDependencies = Seq(
    spotbugs
  )
}
