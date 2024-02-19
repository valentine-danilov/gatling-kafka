package io.github.vdanilau.gatling.kafka.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session
import io.github.vdanilau.gatling.kafka.client.KafkaTrackerPool
import org.apache.kafka.clients.producer.KafkaProducer

final class KafkaComponents(
  val kafkaProtocol: KafkaProtocol,
  val kafkaProducer: KafkaProducer[Any, Any],
  val trackerPool: Option[KafkaTrackerPool]
) extends ProtocolComponents {
  
  override def onStart: Session => Session = Session.Identity
  
  override def onExit: Session => Unit = ProtocolComponents.NoopOnExit
}
