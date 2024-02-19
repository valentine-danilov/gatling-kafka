package io.github.vdanilau.javaapi.kafka.internal

import io.github.vdanilau.gatling.javaapi.kafka.KafkaMessageMatcher
import io.github.vdanilau.gatling.kafka.protocol.{KafkaMessageMatcher => ScalaKafkaMessageMatcher}

object KafkaMessageMatchers {
  def toScala(javaMatcher: KafkaMessageMatcher[_, _, _, _]): ScalaKafkaMessageMatcher[_, _, _, _] =
    javaMatcher.asInstanceOf[ScalaKafkaMessageMatcher[_, _, _, _]]
}
