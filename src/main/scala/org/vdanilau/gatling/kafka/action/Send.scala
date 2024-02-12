package org.vdanilau.gatling.kafka.action

import io.gatling.commons.stats.OK
import io.gatling.commons.util.Clock
import io.gatling.commons.validation.{Success, Validation}
import io.gatling.core.action.Action
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.vdanilau.gatling.kafka.protocol.KafkaProtocol
import org.vdanilau.gatling.kafka.request.{KafkaAttributes, KafkaMessage}

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
