package org.vdanilau.gatling.kafka.request


final case class KafkaMessage(
  topic: String,
  key: Option[Any],
  value: Any,
  headers: Option[Map[String, String]]
)
