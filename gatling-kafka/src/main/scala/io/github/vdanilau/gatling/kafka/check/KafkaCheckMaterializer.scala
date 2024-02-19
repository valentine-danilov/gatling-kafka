package io.github.vdanilau.gatling.kafka.check

import io.gatling.commons.validation._
import io.gatling.core.check.{CheckMaterializer, Preparer}
import io.github.vdanilau.gatling.kafka.KafkaCheck
import org.apache.kafka.clients.consumer.ConsumerRecord

final class KafkaCheckMaterializer[T, P](
  override val preparer: Preparer[ConsumerRecord[Any, Any], P]
) extends CheckMaterializer[T, KafkaCheck, ConsumerRecord[Any, Any], P](identity)

object KafkaCheckMaterializer {
  private def specificConsumerRecordPreparer[K, V](): Preparer[ConsumerRecord[Any, Any], ConsumerRecord[K, V]] = {
    replyMessage => {
      replyMessage match {
        case cr: ConsumerRecord[K, V] => cr.success
        case _ => s"Unable to cast to ConsumerRecord".failure
      }
    }
  }
}
