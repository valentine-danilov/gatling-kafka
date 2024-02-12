package org.vdanilau.gatling.kafka.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session
import org.apache.kafka.clients.producer.KafkaProducer
import org.vdanilau.gatling.kafka.client.KafkaTrackerPool

final class KafkaComponents(
  val kafkaProtocol: KafkaProtocol,
  val kafkaProducer: KafkaProducer[Any, Any],
  val trackerPool: KafkaTrackerPool
) extends ProtocolComponents {
  
  override def onStart: Session => Session = Session.Identity
  
  override def onExit: Session => Unit = ProtocolComponents.NoopOnExit
}
