package io.github.vdanilau.gatling.kafka.client

import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.WakeupException

import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
import scala.jdk.CollectionConverters.IterableHasAsJava

final class KafkaListener(
  kafkaConsumer: KafkaConsumer[Any, Any],
  topic: String,
  pollInterval: Duration,
  handler: ConsumerRecord[Any, Any] => Unit
) extends Runnable {
  
  private val closed = new AtomicBoolean()
  
  override def run(): Unit = {
    try {
      kafkaConsumer.subscribe(List(topic).asJavaCollection)
      kafkaConsumer.seekToEnd(List.empty[TopicPartition].asJavaCollection)
      while (!closed.get()) {
        kafkaConsumer.poll(pollInterval)
          .records(topic)
          .forEach { record =>
            handler(record)
          }
      }
    } catch {
      case e: WakeupException => if (!closed.get()) throw e
    } finally {
      kafkaConsumer.close()
    }
  }
  
  def shutdown(): Unit = {
    closed.set(true)
    kafkaConsumer.wakeup()
  }
}
