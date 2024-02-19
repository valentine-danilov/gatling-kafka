package io.github.vdanilau.gatling.javaapi.kafka;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

public interface KafkaMessageMatcher<K, V, RK, RV> {

    void prepareRequest(@NonNull ProducerRecord<K, V> producerRecord);

    void requestMatchId(@NonNull ProducerRecord<K, V> producerRecord);

    void responseMatchId(@NonNull ConsumerRecord<RK, RV> consumerRecord);

}
