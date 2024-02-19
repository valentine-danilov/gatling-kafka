package io.github.vdanilau.gatling.kafka.action

import io.gatling.commons.stats.OK
import io.gatling.commons.util.Clock
import io.gatling.commons.validation.{Success, Validation}
import io.gatling.core.action.Action
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.github.vdanilau.gatling.kafka.protocol.KafkaProtocol
import io.github.vdanilau.gatling.kafka.request.KafkaAttributes
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

class Send(
  attributes: KafkaAttributes,
  protocol: KafkaProtocol,
  producer: KafkaProducer[Any, Any],
  throttler: Option[Throttler],
  val statsEngine: StatsEngine,
  val clock: Clock,
  val next: Action,
) extends KafkaAction(attributes, protocol, producer, throttler) {
  override def name: String = genName("kafkaSend")
  
  override protected def aroundSend(requestName: String, session: Session, message: ProducerRecord[Any, Any]): Validation[Around] =
    new Success[Around](
      new Around(
        before = () => {
          val now = clock.nowMillis
          statsEngine.logResponse(session.scenario, session.groups, requestName, now, now, OK, None, None)
          next ! session
        },
        after = () => ()
      )
    )
}
