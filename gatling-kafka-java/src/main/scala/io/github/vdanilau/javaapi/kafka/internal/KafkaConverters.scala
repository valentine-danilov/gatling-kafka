package io.github.vdanilau.javaapi.kafka.internal

import io.gatling.core.session.Expression
import io.gatling.javaapi.core.internal.Expressions._
import io.gatling.javaapi.core.{Session => JavaSession}

import java.util.function.{Function => JavaFunction}
import scala.jdk.CollectionConverters.MapHasAsScala

object KafkaConverters {
  
  def stringHeadersToScala(headers: java.util.Map[String, String]): Map[Expression[String], Expression[String]] =
    headers
      .asScala
      .map { case (key, value) => (toStringExpression(key), toStringExpression(value)) }
      .toMap
  
  def expressionHeadersToScala(headers: java.util.Map[JavaFunction[JavaSession, String], JavaFunction[JavaSession, String]])
  : Map[Expression[String], Expression[String]] =
    headers
      .asScala
      .map { case (key, value) => (javaFunctionToExpression(key), javaFunctionToExpression(value)) }
      .toMap
}
