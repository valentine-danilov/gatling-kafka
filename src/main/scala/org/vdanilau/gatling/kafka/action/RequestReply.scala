package org.vdanilau.gatling.kafka.action

import io.gatling.commons.stats.KO
import io.gatling.commons.util.Clock
import io.gatling.commons.validation.Validation
import io.gatling.core.action.Action
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.{Session, resolveOptionalExpression}
import io.gatling.core.stats.StatsEngine
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.vdanilau.gatling.kafka.client.KafkaTrackerPool
import org.vdanilau.gatling.kafka.protocol.KafkaProtocol
import org.vdanilau.gatling.kafka.request.KafkaAttributes

final class RequestReply(
  trackerPool: KafkaTrackerPool,
  attributes: KafkaAttributes,
  protocol: KafkaProtocol,
  producer: KafkaProducer[Any, Any],
  throttler: Option[Throttler],
  val statsEngine: StatsEngine,
  val clock: Clock,
  val next: Action,
) extends KafkaAction(attributes, protocol, producer, throttler) {
  
  override def name: String = genName("kafkaRequestReply")
  
  private val replyTimeoutMs = protocol.replyTimeout.fold(0L)(_.toMillis)
  private val messageMatcher = attributes.messageMatcher.get
  
  override protected def aroundSend(requestName: String, session: Session, message: ProducerRecord[Any, Any]): Validation[Around] = {
    for {
      resolvedReplyTopic <- resolveOptionalExpression(attributes.replyTopic, session)
    } yield {
      
      messageMatcher.prepareRequest(message)
      
      val matchId = messageMatcher.requestMatchId(message)
      val tracker = trackerPool.tracker(resolvedReplyTopic.get, messageMatcher)
      
      new Around(
        before = () => {
          if (matchId != null) {
            tracker.track(matchId, clock.nowMillis, replyTimeoutMs, session, next, requestName)
          }
        },
        after = () => {
          if (matchId == null) {
            val updatedMatchId = messageMatcher.requestMatchId(message)
            if (updatedMatchId != null) {
              tracker.track(updatedMatchId, clock.nowMillis, replyTimeoutMs, session, next, requestName)
            } else {
              val now = clock.nowMillis
              statsEngine.logResponse(session.scenario, session.groups, requestName, now, now, KO, None, Some("Failed to get a matchId to track"))
              next ! session.markAsFailed
            }
          }
        }
      )
    }
  }
  
  
}
