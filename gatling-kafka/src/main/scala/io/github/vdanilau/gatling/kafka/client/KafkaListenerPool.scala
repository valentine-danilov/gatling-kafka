package io.github.vdanilau.gatling.kafka.client

import io.netty.util.concurrent.DefaultThreadFactory
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import KafkaListenerPool.KafkaListenerThreadFactory

import java.time.Duration
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters.MapHasAsJava

object KafkaListenerPool {
  private val KafkaListenerThreadFactory = new DefaultThreadFactory("gatling-kafka-consumer")
}

final class KafkaListenerPool(
  val consumerConfiguration: Map[String, AnyRef]
) {
  private val listeners = ArrayBuffer[KafkaListener]()
  
  def startListener(topic: String, pollInterval: Duration = Duration.ofMillis(100), handler: ConsumerRecord[Any, Any] => Unit): Unit = {
    val kafkaConsumer = new KafkaConsumer[Any, Any](consumerConfiguration.asJava)
    val listener = new KafkaListener(kafkaConsumer, topic, pollInterval, handler)
    listeners += listener
    
    val listenerThread = KafkaListenerThreadFactory.newThread(listener)
    listenerThread.start()
  }
  
  def close(): Unit = listeners.foreach(_.shutdown())
}
