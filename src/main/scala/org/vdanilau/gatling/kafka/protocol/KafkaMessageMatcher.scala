package org.vdanilau.gatling.kafka.protocol

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord

/**
 *
 * @tparam K producer record key type
 * @tparam V producer record value type
 * @tparam RK consumer record key type
 * @tparam RV consumer record value type
 */
trait KafkaMessageMatcher[K >: Null, V, RK >: Null, RV] {
  def prepareRequest(producerRecord: ProducerRecord[K, V]): Unit
  def requestMatchId(producerRecord: ProducerRecord[K, V]): String
  def responseMatchId(consumerRecord: ConsumerRecord[RK, RV]): String
}

object KeyMessageMatcher extends KafkaMessageMatcher[AnyRef, AnyRef, AnyRef, AnyRef] {
  
  override def prepareRequest(producerRecord: ProducerRecord[AnyRef, AnyRef]): Unit = {}
  
  override def requestMatchId(producerRecord: ProducerRecord[AnyRef, AnyRef]): String = producerRecord.key().toString
  
  override def responseMatchId(consumerRecord: ConsumerRecord[AnyRef, AnyRef]): String = consumerRecord.key().toString
}
