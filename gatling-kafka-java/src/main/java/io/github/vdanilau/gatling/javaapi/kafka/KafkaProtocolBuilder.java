package io.github.vdanilau.gatling.javaapi.kafka;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.gatling.core.protocol.Protocol;
import io.gatling.javaapi.core.ProtocolBuilder;
import io.gatling.javaapi.core.internal.Converters;

import java.time.Duration;
import java.util.Map;

public class KafkaProtocolBuilder implements ProtocolBuilder {

    public static final class Base {

        public static final Base INSTANCE = new Base();

        private Base() {
        }

        @NonNull
        public KafkaProtocolBuilder producerProperties(@NonNull Map<String, String> producerProperties) {
            return new KafkaProtocolBuilder(
                    io.github.vdanilau.gatling.kafka.protocol.KafkaProtocolBuilderBase.producerProperties(Converters.toScalaMap(producerProperties))
            );
        }

    }

    private final io.github.vdanilau.gatling.kafka.protocol.KafkaProtocolBuilder wrapped;

    public KafkaProtocolBuilder(io.github.vdanilau.gatling.kafka.protocol.KafkaProtocolBuilder wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Protocol protocol() {
        return wrapped.build();
    }

    @NonNull
    public KafkaProtocolBuilder consumerProperties(@NonNull Map<String, String> consumerProperties) {
        return new KafkaProtocolBuilder(
                wrapped.consumerProperties(Converters.toScalaMap(consumerProperties))
        );
    }

    public KafkaProtocolBuilder replyTimeout(@NonNull Duration replyTimeout) {
        return new KafkaProtocolBuilder(
                wrapped.replyTimeout(Converters.toScalaDuration(replyTimeout))
        );
    }


}
