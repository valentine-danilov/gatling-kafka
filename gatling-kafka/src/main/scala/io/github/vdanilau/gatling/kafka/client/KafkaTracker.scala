package io.github.vdanilau.gatling.kafka.client

import akka.actor.ActorRef
import io.gatling.core.action.Action
import io.gatling.core.session.Session
import io.github.vdanilau.gatling.kafka.KafkaCheck

final class KafkaTracker(actor: ActorRef) {
  def track(
    matchId: String,
    sent: Long,
    replyTimeoutMs: Long,
    checks: List[KafkaCheck],
    session: Session,
    next: Action,
    requestName: String
  ): Unit = {
    actor ! MessageSent(
      matchId,
      sent,
      replyTimeoutMs,
      checks,
      session,
      next,
      requestName
    )
  }
}
