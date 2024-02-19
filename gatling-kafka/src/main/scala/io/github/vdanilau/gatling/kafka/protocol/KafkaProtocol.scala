package io.github.vdanilau.gatling.kafka.protocol

import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}
import io.github.vdanilau.gatling.kafka.client.{KafkaListenerPool, KafkaTrackerPool}
import org.apache.kafka.clients.producer.KafkaProducer

import scala.concurrent.duration.FiniteDuration
import scala.jdk.CollectionConverters.MapHasAsJava

object KafkaProtocol {
  val KafkaProtocolKey: ProtocolKey[KafkaProtocol, KafkaComponents] = new ProtocolKey[KafkaProtocol, KafkaComponents] {
    override def protocolClass: Class[Protocol] = classOf[KafkaProtocol].asInstanceOf[Class[Protocol]]
    
    override def defaultProtocolValue(configuration: GatlingConfiguration): KafkaProtocol =
      throw new IllegalStateException("Can't provide a default value for KafkaProtocol")
    
    override def newComponents(coreComponents: CoreComponents): KafkaProtocol => KafkaComponents = {
      kafkaProtocol => {
        val producerProperties = kafkaProtocol.producerProperties.asJava
        val consumerProperties = kafkaProtocol.consumerProperties
        val kafkaProducer = new KafkaProducer[Any, Any](producerProperties)
        
        val trackerPool = consumerProperties match {
          case Some(properties) =>
            val kafkaListenerPool = new KafkaListenerPool(properties)
            Some(
              new KafkaTrackerPool(
                kafkaListenerPool,
                coreComponents.actorSystem,
                coreComponents.statsEngine,
                coreComponents.clock,
                coreComponents.configuration
              )
            )
          case _ => None
        }
        
        coreComponents.actorSystem.registerOnTermination {
          kafkaProducer.close()
          trackerPool match {
            case Some(pool) => pool.close()
            case _ => ()
          }
        }
        
        new KafkaComponents(kafkaProtocol, kafkaProducer, trackerPool)
      }
    }
  }
}

final case class KafkaProtocol(
  producerProperties: Map[String, AnyRef],
  consumerProperties: Option[Map[String, AnyRef]],
  replyTimeout: Option[FiniteDuration],
) extends Protocol {
  type Components = KafkaComponents
}
