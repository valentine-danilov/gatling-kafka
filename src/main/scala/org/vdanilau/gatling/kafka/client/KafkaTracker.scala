package org.vdanilau.gatling.kafka.client

import akka.actor.ActorRef
import io.gatling.core.action.Action
import io.gatling.core.session.Session

final class KafkaTracker(actor: ActorRef) {
  def track(
    matchId: String,
    sent: Long,
    replyTimeoutMs: Long,
    session: Session,
    next: Action,
    requestName: String
  ): Unit = {
    actor ! MessageSent(
      matchId,
      sent, replyTimeoutMs, session, next, requestName
    )
  }
}
