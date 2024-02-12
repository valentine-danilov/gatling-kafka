package org.vdanilau.gatling.kafka.protocol

import io.gatling.core.session.Expression

import scala.concurrent.duration.FiniteDuration

case object KafkaProtocolBuilderBase {
  def producerProperties(producerProperties: Map[String, String]): KafkaProtocolBuilder =
    KafkaProtocolBuilder(producerProperties, None, None)
}

final case class KafkaProtocolBuilder(
  producerProperties: Map[String, String],
  consumerStreamProperties: Option[Map[String, AnyRef]],
  replyTimeout: Option[FiniteDuration],
) {
  def consumerProperties(properties: Map[String, AnyRef]): KafkaProtocolBuilder =
    copy(consumerStreamProperties = Some(properties))
    
  def replyTimeout(timeout: FiniteDuration): KafkaProtocolBuilder =
    copy(replyTimeout = Some(timeout))
  
  def build: KafkaProtocol = KafkaProtocol(
    producerProperties,
    consumerStreamProperties,
    replyTimeout
  )
}
