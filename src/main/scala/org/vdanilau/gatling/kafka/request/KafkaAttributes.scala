package org.vdanilau.gatling.kafka.request

import io.gatling.core.session.Expression
import org.vdanilau.gatling.kafka.protocol.KafkaMessageMatcher

final case class KafkaAttributes(
  requestName: Expression[String],
  topic: Expression[String],
  key: Option[Expression[Any]],
  value: Expression[Any],
  headers: Option[Map[Expression[String], Expression[String]]],
  replyTopic: Option[Expression[String]],
  messageMatcher: Option[KafkaMessageMatcher[Any, Any, Any, Any]]
)
