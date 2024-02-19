package io.github.vdanilau.gatling.kafka.check

import io.gatling.commons.validation.FailureWrapper
import io.gatling.core.check.Check.PreparedCache
import io.gatling.core.check.{Check, CheckResult}
import io.gatling.core.session.Session
import io.github.vdanilau.gatling.kafka.KafkaCheck
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.header.{Header, Headers}

trait KafkaCheckSupport {
  
  def simpleCheck(errorMessage: String = "Kafka check failed", f: ConsumerRecord[Any, Any] => Boolean): KafkaCheck =
    Check.Simple(
      (response: ConsumerRecord[Any, Any], _: Session, _: PreparedCache) => {
        if (f(response)) {
          CheckResult.NoopCheckResultSuccess
        } else {
          errorMessage.failure
        }
      },
      None
    )
  
  def consumerRecordCheck[K, V](errorMessage: String = "Kafka ConsumerRecord check failed", f: ConsumerRecord[K, V] => Boolean): KafkaCheck =
    Check.Simple(
      (response: ConsumerRecord[Any, Any], _: Session, _: PreparedCache) => {
        response match {
          case cr: ConsumerRecord[K, V] =>
            if (f(cr)) {
              CheckResult.NoopCheckResultSuccess
            } else {
              errorMessage.failure
            }
          case _ => s"Unable to cast consumed record to specified types".failure
        }
      },
      None
    )
  
  def keyCheck[K](errorMessage: String = "Kafka key check failed", f: K => Boolean): KafkaCheck =
    Check.Simple(
      (response: ConsumerRecord[Any, Any], _: Session, _: PreparedCache) => {
        response.key() match {
          case key: K =>
            if (f(key)) {
              CheckResult.NoopCheckResultSuccess
            } else {
              errorMessage.failure
            }
          case _ => s"Unable to cast to consumer record's key to specified type".failure
        }
      },
      None
    )
  
  def valueCheck[V](errorMessage: String = "Kafka value check failed", f: V => Boolean): KafkaCheck =
    Check.Simple(
      (response: ConsumerRecord[Any, Any], _: Session, _: PreparedCache) => {
        response.value() match {
          case value: V =>
            if (f(value)) {
              CheckResult.NoopCheckResultSuccess
            } else {
              errorMessage.failure
            }
          case _ => s"Unable to cast consumed record's value to specified type".failure
        }
      },
      None
    )
  
  def headersCheck(f: Headers => Boolean, errorMessage: String = "Kafka headers check failed"): KafkaCheck =
    Check.Simple(
      (response: ConsumerRecord[Any, Any], _: Session, _: PreparedCache) => {
        if (f(response.headers())) {
          CheckResult.NoopCheckResultSuccess
        } else {
          errorMessage.failure
        }
      },
      None
    )
  
  def headerCheck(headerKey: String,  f: Header => Boolean, errorMessage: String = "Kafka header check failed"): KafkaCheck =
    Check.Simple(
      (response: ConsumerRecord[Any, Any], _: Session, _: PreparedCache) => {
        if (f(response.headers().lastHeader(headerKey))) {
          CheckResult.NoopCheckResultSuccess
        } else {
          errorMessage.failure
        }
      },
      None
    )
}
