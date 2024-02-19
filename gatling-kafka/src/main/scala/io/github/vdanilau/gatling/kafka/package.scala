package io.github.vdanilau.gatling

import io.gatling.core.check.Check
import org.apache.kafka.clients.consumer.ConsumerRecord

package object kafka {
  type KafkaCheck = Check[ConsumerRecord[Any, Any]]
}
