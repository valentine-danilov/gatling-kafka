package io.github.vdanilau.gatling.javaapi.kafka;

import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.core.internal.Expressions;

import java.util.function.Function;

public final class KafkaDsl {

    private KafkaDsl() {
    }

    public static final KafkaProtocolBuilder.Base kafka = KafkaProtocolBuilder.Base.INSTANCE;

    public static Kafka kafka(String name) {
        return new Kafka(Expressions.toStringExpression(name));
    }

    public static Kafka kafka(Function<Session, String> name) {
        return new Kafka(Expressions.javaFunctionToExpression(name));
    }
}
