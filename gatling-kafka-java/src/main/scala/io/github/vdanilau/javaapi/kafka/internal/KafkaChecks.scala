package io.github.vdanilau.javaapi.kafka.internal

import io.gatling.javaapi.core.internal.Converters
import io.github.vdanilau.gatling.javaapi.kafka.internal.KafkaCheckType
import io.github.vdanilau.gatling.kafka
import io.github.vdanilau.gatling.kafka.{KafkaCheck, Predef}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.header.{Header, Headers}

import java.{util => ju}
import java.util.{function => juf}
import scala.jdk.CollectionConverters.CollectionHasAsScala

object KafkaChecks {
  private def toScalaCheck(javaCheck: io.gatling.javaapi.core.CheckBuilder): KafkaCheck = {
    val scalaCheck = javaCheck.asScala
    javaCheck.`type` match {
      case KafkaCheckType.Simple => scalaCheck.build(null).asInstanceOf[KafkaCheck]
      case unknown => throw new IllegalArgumentException(s"Kafka DSL doesn't support $unknown")
    }
  }
  
  def toScalaChecks(javaChecks: ju.List[io.gatling.javaapi.core.CheckBuilder]): Seq[KafkaCheck] =
    javaChecks.asScala.map(toScalaCheck).toSeq
  
  def simpleChecks(f: ju.List[juf.Function[ConsumerRecord[_, _], Boolean]]): Seq[KafkaCheck] =
    f.asScala.map { f =>
      Predef.simpleCheck("Kafka check failed", Converters.toScalaFunction(f))
    }.toSeq
  
  def consumerRecordCheck[K, V](errorMessage: String, f: juf.Function[ConsumerRecord[K, V], Boolean]): Seq[KafkaCheck] =
    Seq(kafka.Predef.consumerRecordCheck(errorMessage, Converters.toScalaFunction(f)))
    
  def valueCheck[V](errorMessage: String, f: juf.Function[V, Boolean]): Seq[KafkaCheck] =
    Seq(kafka.Predef.valueCheck(errorMessage, Converters.toScalaFunction(f)))
    
  def keyCheck[K](errorMessage: String, f: juf.Function[K, Boolean]): Seq[KafkaCheck] =
    Seq(kafka.Predef.keyCheck(errorMessage, Converters.toScalaFunction(f)))
    
  def headerCheck(errorMessage: String, headerKey: String, f: juf.Function[Header, Boolean]): Seq[KafkaCheck] =
    Seq(kafka.Predef.headerCheck(headerKey, Converters.toScalaFunction(f), errorMessage))
    
  def headersCheck(errorMessage: String, f: juf.Function[Headers, Boolean]): Seq[KafkaCheck] =
    Seq(kafka.Predef.headersCheck(Converters.toScalaFunction(f), errorMessage))
}
