package org.vdanilau.gatling.kafka.action

import io.gatling.core.action.Action
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioContext
import org.vdanilau.gatling.kafka.client.KafkaTrackerPool
import org.vdanilau.gatling.kafka.protocol.KafkaMessageMatcher
import org.vdanilau.gatling.kafka.request.KafkaAttributes

import scala.concurrent.duration.FiniteDuration

final case class RequestReplyBuilder(
  kafkaAttributes: KafkaAttributes,
) extends KafkaActionBuilder {
  
  override def build(ctx: ScenarioContext, next: Action): Action = {
    val kafkaComponents = components(ctx.protocolComponentsRegistry)
    new RequestReply(
      kafkaComponents.trackerPool,
      kafkaAttributes,
      kafkaComponents.kafkaProtocol,
      kafkaComponents.kafkaProducer,
      ctx.coreComponents.throttler,
      ctx.coreComponents.statsEngine,
      ctx.coreComponents.clock,
      next
    )
  }
}
