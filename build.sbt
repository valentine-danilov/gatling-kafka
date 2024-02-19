import Dependencies._
import BuildSettings._

Global / version := "1.0.0"
Global / name := "gatling-kafka"
Global / scalaVersion := "2.13.12"

Global / resolvers += "confluent" at "https://packages.confluent.io/maven/"

lazy val root = Project("gatling-kafka-parent", file("."))
  .aggregate(kafka, kafkaJava)

lazy val kafka = Project("gatling-kafka", file("gatling-kafka"))
  .settings(libraryDependencies ++= kafkaDependencies)

lazy val kafkaJava = Project("gatling-kafka-java", file("gatling-kafka-java"))
  .dependsOn(kafka % "compile->compile;test->test")
  .settings(libraryDependencies ++= kafkaJavaDependencies)
