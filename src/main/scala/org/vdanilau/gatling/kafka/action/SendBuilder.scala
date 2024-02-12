package org.vdanilau.gatling.kafka.action

import io.gatling.core.action.Action
import io.gatling.core.structure.ScenarioContext
import org.apache.kafka.clients.producer.KafkaProducer
import org.vdanilau.gatling.kafka.request.KafkaAttributes

class SendBuilder(
  attributes: KafkaAttributes
) extends KafkaActionBuilder {
  override def build(ctx: ScenarioContext, next: Action): Action = {
    val kafkaComponents = components(ctx.protocolComponentsRegistry)
    new Send(
      attributes,
      kafkaComponents.kafkaProtocol,
      kafkaComponents.kafkaProducer,
      ctx.coreComponents.throttler,
      ctx.coreComponents.statsEngine,
      ctx.coreComponents.clock,
      next
    )
  }
}
