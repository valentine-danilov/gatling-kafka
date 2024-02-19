package io.github.vdanilau.gatling.kafka.client

import akka.actor.{Actor, Props, Timers}
import com.typesafe.scalalogging.LazyLogging
import io.gatling.commons.stats.{KO, OK, Status}
import io.gatling.commons.util.Clock
import io.gatling.commons.validation.Failure
import io.gatling.core.action.Action
import io.gatling.core.check.Check
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.github.vdanilau.gatling.kafka.KafkaCheck
import org.apache.kafka.clients.consumer.ConsumerRecord

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

final case class MessageSent(
  matchId: String,
  sent: Long,
  replyTimeoutInMs: Long,
  checks: List[KafkaCheck],
  session: Session,
  next: Action,
  requestName: String
)

final case class MessageReceived(
  matchId: String,
  received: Long,
  message: ConsumerRecord[Any, Any]
)

case object TimeoutScan

object Tracker {
  def props(statsEngine: StatsEngine, clock: Clock, configuration: GatlingConfiguration): Props =
    Props(new Tracker(statsEngine, clock, configuration.jms.replyTimeoutScanPeriod))
}

final class Tracker[K >: Null, V](
  statsEngine: StatsEngine,
  clock: Clock,
  replyTimeoutScanPeriod: FiniteDuration
) extends Actor with Timers with LazyLogging {
  
  private val sentMessages = mutable.HashMap.empty[String, MessageSent]
  private val timedOutMessages = mutable.ArrayBuffer.empty[MessageSent]
  private var periodicTimeoutScanTriggered = false
  
  private def triggerPeriodicTimeoutScan(): Unit =
    if (!periodicTimeoutScanTriggered) {
      periodicTimeoutScanTriggered = true
      timers.startTimerAtFixedRate("timeoutTimer", TimeoutScan, replyTimeoutScanPeriod)
    }
  
  override def receive: Receive = {
    case messageSent: MessageSent =>
      sentMessages += messageSent.matchId -> messageSent
      if (messageSent.replyTimeoutInMs > 0) {
        triggerPeriodicTimeoutScan()
      }
    case MessageReceived(matchId, received, message) =>
      sentMessages.remove(matchId).foreach {
        case MessageSent(_, sent, _, checks, session, next, requestName) =>
          processMessage(session, sent, received, checks, message, next, requestName)
      }
    case TimeoutScan =>
      val now = clock.nowMillis
      sentMessages.valuesIterator.foreach { message =>
        val replyTimeoutInMs = message.replyTimeoutInMs
        if (replyTimeoutInMs > 0 && (now - message.sent) > replyTimeoutInMs) {
          timedOutMessages += message
        }
        for (MessageSent(matchId, sent, replyTimeoutInMs, _, session, next, requestName) <- timedOutMessages) {
          sentMessages.remove(matchId)
          executeNext(session.markAsFailed, sent, now, KO, next, requestName, Some(s"Reply timeout after $replyTimeoutInMs ms"))
        }
        timedOutMessages.clear()
      }
  }
  
  private def executeNext(
    session: Session,
    sent: Long,
    received: Long,
    status: Status,
    next: Action,
    requestName: String,
    message: Option[String]
  ): Unit = {
    statsEngine.logResponse(session.scenario, session.groups, requestName, sent, received, status, None, message)
    next ! session.logGroupRequestTimings(sent, received)
  }
  
  private def processMessage(
    session: Session,
    sent: Long,
    received: Long,
    checks: List[KafkaCheck],
    message: ConsumerRecord[Any, Any],
    next: Action,
    requestName: String
  ): Unit = {
    val (newSession, error) = Check.check(message, session, checks)
    error match {
      case Some(Failure(errorMessage)) => executeNext(newSession.markAsFailed, sent, received, KO, next, requestName, Some(errorMessage))
      case _ => executeNext(session, sent, received, OK, next, requestName, None)
    }
  }
}
