package io.github.vdanilau.gatling.kafka.action

import io.gatling.commons.validation.Validation
import io.gatling.core.action.RequestAction
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.{Expression, Session, resolveOptionalExpression}
import io.gatling.core.util.NameGen
import io.github.vdanilau.gatling.kafka.protocol.KafkaProtocol
import io.github.vdanilau.gatling.kafka.request.KafkaAttributes
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader

import scala.jdk.CollectionConverters.IterableHasAsJava

class Around(before: () => Unit, after: () => Unit) {
  def apply(f: => Any): Unit = {
    before()
    f
    after()
  }
}

abstract class KafkaAction(
  attributes: KafkaAttributes,
  protocol: KafkaProtocol,
  producer: KafkaProducer[Any, Any],
  throttler: Option[Throttler]
) extends RequestAction
  with NameGen {
  override val requestName: Expression[String] = attributes.requestName
  private val topic = attributes.topic
  
  override def sendRequest(session: Session): Validation[Unit] =
    for {
      resolvedRequestName <- requestName(session)
      resolvedTopic <- topic(session)
      resolvedKey <- resolveOptionalExpression(attributes.key, session)
      resolvedValue <- attributes.value(session)
      resolvedHeaders = resolveHeaders(attributes.headers, session)
      producerRecord = new ProducerRecord[Any, Any](resolvedTopic, null, null, resolvedKey.orNull, resolvedValue, resolvedHeaders.orNull.asJava)
      around <- aroundSend(resolvedRequestName, session, producerRecord)
    } yield {
      producer.send(producerRecord)
      throttler match {
        case Some(th) => th.throttle(session.scenario, () => around(producerRecord))
        case _ => around(producerRecord)
      }
    }
  
  
  private def resolveHeaders(
    headers: Option[Map[Expression[String], Expression[String]]],
    session: Session
  ): Option[Iterable[Header]] = {
    val resolvedHeaders = headers.map { h =>
      h.map { case (key, value) => (key(session).toOption, value(session).toOption) }
        .collect { case (Some(key), Some(value)) => (key, value) }
    }
    resolvedHeaders match {
      case Some(h) => Some(h map { case (key, value) => new RecordHeader(key, value.getBytes).asInstanceOf[Header] })
      case _ => None
    }
  }
  
  protected def aroundSend(requestName: String, session: Session, message: ProducerRecord[Any, Any]): Validation[Around]
}
