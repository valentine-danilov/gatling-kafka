package io.github.vdanilau.gatling.kafka.action

import io.gatling.core.action.Action
import io.gatling.core.structure.ScenarioContext
import io.github.vdanilau.gatling.kafka.request.KafkaAttributes

final case class RequestReplyBuilder(
  kafkaAttributes: KafkaAttributes,
) extends KafkaActionBuilder {
  
  override def build(ctx: ScenarioContext, next: Action): Action = {
    val kafkaComponents = components(ctx.protocolComponentsRegistry)
    val trackerPool = kafkaComponents.trackerPool match {
      case Some(pool) => pool
      case None => throw new IllegalStateException("KafkaTrackerPool was not initialized. Consumer properties are not set?")
    }
    new RequestReply(
      trackerPool,
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
