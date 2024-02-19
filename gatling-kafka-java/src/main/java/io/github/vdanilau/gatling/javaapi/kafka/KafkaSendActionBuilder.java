package io.github.vdanilau.gatling.javaapi.kafka;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.core.internal.Expressions;
import io.github.vdanilau.gatling.kafka.request.SendDslBuilder;import io.github.vdanilau.javaapi.kafka.internal.KafkaConverters;

import java.util.Map;
import java.util.function.Function;

import static io.gatling.javaapi.core.internal.Expressions.javaFunctionToExpression;
import static io.gatling.javaapi.core.internal.Expressions.toStaticValueExpression;

public final class KafkaSendActionBuilder implements ActionBuilder {

    private final SendDslBuilder wrapped;

    public KafkaSendActionBuilder(SendDslBuilder wrapped) {
        this.wrapped = wrapped;
    }

    @NonNull
    public KafkaSendActionBuilder header(@NonNull String key, @NonNull String value) {
        return new KafkaSendActionBuilder(
                wrapped.header(Expressions.toStringExpression(key), Expressions.toStringExpression(value))
        );
    }

    @NonNull
    public KafkaSendActionBuilder header(@NonNull Function<Session, String> key, @NonNull String value) {
        return new KafkaSendActionBuilder(
                wrapped.header(javaFunctionToExpression(key), Expressions.toStringExpression(value))
        );
    }


    @NonNull
    public KafkaSendActionBuilder header(@NonNull String key, @NonNull Function<Session, String> value) {
        return new KafkaSendActionBuilder(
                wrapped.header(Expressions.toStringExpression(key), javaFunctionToExpression(value))
        );
    }

    @NonNull
    public KafkaSendActionBuilder header(@NonNull Function<Session, String> key, @NonNull Function<Session, String> value) {
        return new KafkaSendActionBuilder(
                wrapped.header(javaFunctionToExpression(key), javaFunctionToExpression(value))
        );
    }

    @Override
    public io.gatling.core.action.builder.ActionBuilder asScala() {
        return wrapped.build();
    }

    public static final class Topic {
        private final SendDslBuilder.Topic wrapped;

        public Topic(SendDslBuilder.Topic wrapped) {
            this.wrapped = wrapped;
        }

        @NonNull
        public Message topic(@NonNull String topic) {
            return new Message(wrapped.topic(Expressions.toStringExpression(topic)));
        }

        @NonNull
        Message topic(@NonNull Function<Session, String> topic) {
            return new Message(wrapped.topic(javaFunctionToExpression(topic)));
        }

    }

    public static final class Message {
        SendDslBuilder.Message wrapped;

        public Message(SendDslBuilder.Message wrapped) {
            this.wrapped = wrapped;
        }


        @NonNull
        public KafkaSendActionBuilder message(@NonNull Object key, @NonNull Object value) {
            return new KafkaSendActionBuilder(
                    wrapped.message(Expressions.toStaticValueExpression(key), Expressions.toStaticValueExpression(value))
            );
        }

        public KafkaSendActionBuilder message(
                @NonNull Object key,
                @NonNull Function<Session, Object> value
        ) {
            return new KafkaSendActionBuilder(
                    wrapped.message(toStaticValueExpression(key), javaFunctionToExpression(value))
            );
        }

        @NonNull
        public KafkaSendActionBuilder message(
                @NonNull Function<Session, Object> key,
                @NonNull Object value
        ) {
            return new KafkaSendActionBuilder(
                    wrapped.message(javaFunctionToExpression(key), toStaticValueExpression(value))
            );
        }

        public KafkaSendActionBuilder message(
                @NonNull Function<Session, Object> key,
                @NonNull Function<Session, Object> value
        ) {
            return new KafkaSendActionBuilder(
                    wrapped.message(javaFunctionToExpression(key), javaFunctionToExpression(value))
            );
        }

        @NonNull
        public KafkaSendActionBuilder message(@NonNull Object value) {
            return new KafkaSendActionBuilder(
                    wrapped.message(Expressions.toStaticValueExpression(value))
            );
        }

        @NonNull
        public KafkaSendActionBuilder message(@NonNull Function<Session, Object> value) {
            return new KafkaSendActionBuilder(
                    wrapped.message(javaFunctionToExpression(value))
            );
        }

        @NonNull
        public KafkaSendActionBuilder message(@NonNull Object value, @NonNull Map<String, String> headers) {
            return new KafkaSendActionBuilder(
                    wrapped.message(
                            Expressions.toStaticValueExpression(value),
                            KafkaConverters.stringHeadersToScala(headers)
                    )
            );
        }

        @NonNull
        public KafkaSendActionBuilder message(
                @NonNull Function<Session, Object> value,
                @NonNull Map<Function<Session, String>, Function<Session, String>> headers
        ) {
            return new KafkaSendActionBuilder(
                    wrapped.message(
                            javaFunctionToExpression(value),
                            KafkaConverters.expressionHeadersToScala(headers)
                    )
            );
        }

        @NonNull
        public KafkaSendActionBuilder message(@NonNull Object key, @NonNull Object value, @NonNull Map<String, String> headers) {
            return new KafkaSendActionBuilder(
                    wrapped.message(
                            Expressions.toStaticValueExpression(key),
                            Expressions.toStaticValueExpression(value),
                            KafkaConverters.stringHeadersToScala(headers)
                    )
            );
        }

        @NonNull
        public KafkaSendActionBuilder message(
                @NonNull Function<Session, Object> key,
                @NonNull Function<Session, Object> value,
                @NonNull Map<Function<Session, String>, Function<Session, String>> headers
        ) {
            return new KafkaSendActionBuilder(
                    wrapped.message(
                            javaFunctionToExpression(key),
                            javaFunctionToExpression(value),
                            KafkaConverters.expressionHeadersToScala(headers)
                    )
            );
        }
    }
}
