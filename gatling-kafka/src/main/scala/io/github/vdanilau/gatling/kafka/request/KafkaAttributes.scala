package io.github.vdanilau.gatling.kafka.request

import io.gatling.core.session.Expression
import io.github.vdanilau.gatling.kafka.KafkaCheck
import io.github.vdanilau.gatling.kafka.protocol.KafkaMessageMatcher

object KafkaAttributes {
  def apply(
    requestName: Expression[String],
    topic: Expression[String],
    key: Option[Expression[Any]],
    value: Expression[Any],
    headers: Option[Map[Expression[String], Expression[String]]]
  ): KafkaAttributes = {
    new KafkaAttributes(
      requestName,
      topic,
      key,
      value,
      headers,
      None,
      None,
      Nil
    )
  }
}

final case class KafkaAttributes(
  requestName: Expression[String],
  topic: Expression[String],
  key: Option[Expression[Any]],
  value: Expression[Any],
  headers: Option[Map[Expression[String], Expression[String]]],
  replyTopic: Option[Expression[String]],
  messageMatcher: Option[KafkaMessageMatcher[Any, Any, Any, Any]],
  checks: List[KafkaCheck]
)
