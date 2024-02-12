package org.vdanilau.gatling.kafka.client

import akka.actor.ActorSystem
import com.typesafe.scalalogging.StrictLogging
import io.gatling.commons.util.Clock
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.stats.StatsEngine
import io.gatling.core.util.NameGen
import io.netty.util.concurrent.DefaultThreadFactory
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.vdanilau.gatling.kafka.protocol.KafkaMessageMatcher

import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

object KafkaTrackerPool {
  private val KafkaConsumerThreadFactory = new DefaultThreadFactory("gatling-kafka-consumer")
}

final class KafkaTrackerPool(
  listenerPool: KafkaListenerPool,
  system: ActorSystem,
  statsEngine: StatsEngine,
  clock: Clock,
  configuration: GatlingConfiguration
) extends StrictLogging
  with NameGen {
  private val trackers = new ConcurrentHashMap[String, KafkaTracker]
  
  def tracker(topic: String, messageMatcher: KafkaMessageMatcher[Any, Any, Any, Any]): KafkaTracker = {
    trackers.computeIfAbsent(
      topic,
      _ => {
        val actor = system.actorOf(Tracker.props(statsEngine, clock, configuration), genName("kafkaTrackerActor"))
        val handler = {record: ConsumerRecord[Any, Any] =>
          val matchId = messageMatcher.responseMatchId(record)
          logger.debug(s"Received message with matchId=$matchId")
          actor ! MessageReceived(matchId, clock.nowMillis, record)
        }
        listenerPool.startListener(
          topic = topic,
          handler = handler
        )
        
        new KafkaTracker(actor)
      }
    )
  }
  
  
}
