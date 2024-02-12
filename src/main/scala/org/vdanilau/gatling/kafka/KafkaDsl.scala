package org.vdanilau.gatling.kafka

import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Expression
import org.vdanilau.gatling.kafka.protocol.{KafkaProtocol, KafkaProtocolBuilder, KafkaProtocolBuilderBase}
import org.vdanilau.gatling.kafka.request.{KafkaDslBuilderBase, RequestReplyDslBuilder, SendDslBuilder}

trait KafkaDsl {
  def kafka(implicit configuration: GatlingConfiguration): KafkaProtocolBuilderBase.type  = KafkaProtocolBuilderBase
  def kafka(requestName: Expression[String]): KafkaDslBuilderBase = new KafkaDslBuilderBase(requestName)
  
  implicit def kafkaProtocolBuilder2KafkaProtocol(builder: KafkaProtocolBuilder): KafkaProtocol = builder.build
  implicit def kafkaSendDslBuilder2ActionBuilder(builder: SendDslBuilder): ActionBuilder = builder.build
  implicit def kafkaRequestReplyDslBuilder2ActionBuilder(builder: RequestReplyDslBuilder): ActionBuilder = builder.build
}
