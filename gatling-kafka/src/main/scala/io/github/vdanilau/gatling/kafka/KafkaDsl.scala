package io.github.vdanilau.gatling.kafka

import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Expression
import io.github.vdanilau.gatling.kafka.check.KafkaCheckSupport
import io.github.vdanilau.gatling.kafka.protocol.{KafkaProtocol, KafkaProtocolBuilder, KafkaProtocolBuilderBase}
import io.github.vdanilau.gatling.kafka.request.{KafkaDslBuilderBase, RequestReplyDslBuilder, SendDslBuilder}

trait KafkaDsl extends KafkaCheckSupport {
  def kafka(implicit configuration: GatlingConfiguration): KafkaProtocolBuilderBase.type  = KafkaProtocolBuilderBase
  def kafka(requestName: Expression[String]): KafkaDslBuilderBase = new KafkaDslBuilderBase(requestName)
  
  implicit def kafkaProtocolBuilder2KafkaProtocol(builder: KafkaProtocolBuilder): KafkaProtocol = builder.build
  implicit def kafkaSendDslBuilder2ActionBuilder(builder: SendDslBuilder): ActionBuilder = builder.build
  implicit def kafkaRequestReplyDslBuilder2ActionBuilder(builder: RequestReplyDslBuilder): ActionBuilder = builder.build
}
