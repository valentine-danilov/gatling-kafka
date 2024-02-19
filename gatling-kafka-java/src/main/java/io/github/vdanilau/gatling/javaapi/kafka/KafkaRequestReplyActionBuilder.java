package io.github.vdanilau.gatling.javaapi.kafka;


import edu.umd.cs.findbugs.annotations.NonNull;
import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.core.internal.Expressions;
import io.github.vdanilau.gatling.kafka.request.RequestReplyDslBuilder;
import io.github.vdanilau.javaapi.kafka.internal.KafkaChecks;
import io.github.vdanilau.javaapi.kafka.internal.KafkaConverters;
import io.github.vdanilau.javaapi.kafka.internal.KafkaMessageMatchers;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static io.gatling.javaapi.core.internal.Expressions.javaFunctionToExpression;

public class KafkaRequestReplyActionBuilder implements ActionBuilder {
    private final RequestReplyDslBuilder wrapped;

    public KafkaRequestReplyActionBuilder(RequestReplyDslBuilder wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public io.gatling.core.action.builder.ActionBuilder asScala() {
        return wrapped.build();
    }

    @NonNull
    public KafkaRequestReplyActionBuilder header(@NonNull String key, @NonNull String value) {
        return new KafkaRequestReplyActionBuilder(
                wrapped.header(Expressions.toStringExpression(key), Expressions.toStringExpression(value))
        );
    }

    @NonNull
    public KafkaRequestReplyActionBuilder header(@NonNull String key, @NonNull Function<Session, String> value) {
        return new KafkaRequestReplyActionBuilder(
                wrapped.header(Expressions.toStringExpression(key), Expressions.javaFunctionToExpression(value))
        );
    }

    @NonNull
    public KafkaRequestReplyActionBuilder header(@NonNull Function<Session, String> key, @NonNull String value) {
        return new KafkaRequestReplyActionBuilder(
                wrapped.header(Expressions.javaFunctionToExpression(key), Expressions.toStringExpression(value))
        );
    }

    @NonNull
    public KafkaRequestReplyActionBuilder header(
            @NonNull Function<Session, String> key,
            @NonNull Function<Session, String> value
    ) {
        return new KafkaRequestReplyActionBuilder(
                wrapped.header(Expressions.javaFunctionToExpression(key), Expressions.javaFunctionToExpression(value))
        );
    }

    @SafeVarargs
    @NonNull
    public final KafkaRequestReplyActionBuilder check(
            @NonNull Function<ConsumerRecord<?, ?>, Boolean>... checks
    ) {
        List<Function<ConsumerRecord<?, ?>, Object>> wrappedChecks = Arrays.stream(checks).map(this::wrapped).toList();
        return new KafkaRequestReplyActionBuilder(
                wrapped.check(KafkaChecks.simpleChecks(wrappedChecks))
        );
    }

    @NonNull
    public <K, V> KafkaRequestReplyActionBuilder consumerRecordCheck(
            String errorMessage,
            Function<ConsumerRecord<K, V>, Boolean> f
    ) {
        return new KafkaRequestReplyActionBuilder(
                wrapped.check(KafkaChecks.consumerRecordCheck(errorMessage, wrapped(f)))
        );
    }

    @NonNull
    public <K, V> KafkaRequestReplyActionBuilder consumerRecordCheck(
            Function<ConsumerRecord<K, V>, Boolean> f
    ) {
        return new KafkaRequestReplyActionBuilder(
                wrapped.check(KafkaChecks.consumerRecordCheck("Kafka consumer record check failed", wrapped(f)))
        );
    }

    @NonNull
    public <K> KafkaRequestReplyActionBuilder keyCheck(
            String errorMessage,
            Function<K, Boolean> f
    ) {
        return new KafkaRequestReplyActionBuilder(
                wrapped.check(KafkaChecks.keyCheck(errorMessage, wrapped(f)))
        );
    }

    @NonNull
    public <K> KafkaRequestReplyActionBuilder keyCheck(
            Function<K, Boolean> f
    ) {
        return new KafkaRequestReplyActionBuilder(
                wrapped.check(KafkaChecks.keyCheck("Kafka key check failed", wrapped(f)))
        );
    }

    @NonNull
    public <V> KafkaRequestReplyActionBuilder valueCheck(
            String errorMessage,
            Function<V, Boolean> f
    ) {
        return new KafkaRequestReplyActionBuilder(
                wrapped.check(KafkaChecks.valueCheck(errorMessage, wrapped(f)))
        );
    }

    @NonNull
    public <V> KafkaRequestReplyActionBuilder valueCheck(
            Function<V, Boolean> f
    ) {
        return new KafkaRequestReplyActionBuilder(
                wrapped.check(KafkaChecks.valueCheck("Kafka value check failed", wrapped(f)))
        );
    }

    @NonNull
    public KafkaRequestReplyActionBuilder headersCheck(
            String errorMessage,
            Function<Headers, Boolean> f
    ) {
        return new KafkaRequestReplyActionBuilder(
                wrapped.check(KafkaChecks.headersCheck(errorMessage, wrapped(f)))
        );
    }

    @NonNull
    public KafkaRequestReplyActionBuilder headersCheck(
            Function<Headers, Boolean> f
    ) {
        return new KafkaRequestReplyActionBuilder(
                wrapped.check(KafkaChecks.headersCheck("Kafka headers check failed", wrapped(f)))
        );
    }

    @NonNull
    public KafkaRequestReplyActionBuilder headerCheck(
            String key,
            String errorMessage,
            Function<Header, Boolean> f
    ) {
        return new KafkaRequestReplyActionBuilder(
                wrapped.check(KafkaChecks.headerCheck(errorMessage, key, wrapped(f)))
        );
    }

    @NonNull
    KafkaRequestReplyActionBuilder headerCheck(
            String key,
            Function<Header, Boolean> f
    ) {
        return new KafkaRequestReplyActionBuilder(
                wrapped.check(KafkaChecks.headerCheck("Kafka header check failed", key, wrapped(f)))
        );
    }

    private <K> Function<K, Object> wrapped(Function<K, Boolean> f) {
        return f::apply;
    }

    public static final class Topic {
        private final RequestReplyDslBuilder.Topic wrapped;

        public Topic(RequestReplyDslBuilder.Topic wrapped) {
            this.wrapped = wrapped;
        }

        @NonNull
        public ReplyTopic topic(@NonNull String topic) {
            return new ReplyTopic(wrapped.topic(Expressions.toStringExpression(topic)));
        }
    }

    public static final class ReplyTopic {
        private final RequestReplyDslBuilder.ReplyTopic wrapped;

        public ReplyTopic(RequestReplyDslBuilder.ReplyTopic wrapped) {
            this.wrapped = wrapped;
        }

        @NonNull
        public MessageMatcher replyTopic(@NonNull String replyTopic) {
            return new MessageMatcher(wrapped.replyTopic(Expressions.toStringExpression(replyTopic)));
        }

        @NonNull
        public MessageMatcher replyTopic(@NonNull Function<Session, String> replyTopic) {
            return new MessageMatcher(wrapped.replyTopic(Expressions.javaFunctionToExpression(replyTopic)));
        }
    }

    public static final class MessageMatcher {
        private final RequestReplyDslBuilder.MessageMatcher wrapped;

        public MessageMatcher(RequestReplyDslBuilder.MessageMatcher wrapped) {
            this.wrapped = wrapped;
        }

        @NonNull
        public Message messageMatcher(@NonNull KafkaMessageMatcher<?, ?, ?, ?> messageMatcher) {
            return new Message(wrapped.messageMatcher(KafkaMessageMatchers.toScala(messageMatcher)));
        }

        @NonNull
        public Message matchByKey() {
            return new Message(wrapped.matchByKey());
        }
    }

    public static final class Message {
        private final RequestReplyDslBuilder.Message wrapped;

        public Message(RequestReplyDslBuilder.Message wrapped) {
            this.wrapped = wrapped;
        }

        @NonNull
        public KafkaRequestReplyActionBuilder message(@NonNull Object key, @NonNull Object value) {
            return new KafkaRequestReplyActionBuilder(
                    wrapped.message(Expressions.toStaticValueExpression(key), Expressions.toStaticValueExpression(value))
            );
        }

        @NonNull
        public KafkaRequestReplyActionBuilder message(
                @NonNull Function<Session, Object> key,
                @NonNull Function<Session, Object> value
        ) {
            return new KafkaRequestReplyActionBuilder(
                    wrapped.message(Expressions.javaFunctionToExpression(key), Expressions.javaFunctionToExpression(value))
            );
        }

        @NonNull
        public KafkaRequestReplyActionBuilder message(@NonNull Object value) {
            return new KafkaRequestReplyActionBuilder(
                    wrapped.message(Expressions.toStaticValueExpression(value))
            );
        }

        @NonNull
        public KafkaRequestReplyActionBuilder message(@NonNull Function<Session, Object> value) {
            return new KafkaRequestReplyActionBuilder(
                    wrapped.message(Expressions.javaFunctionToExpression(value))
            );
        }

        @NonNull
        public KafkaRequestReplyActionBuilder message(@NonNull Object key, @NonNull Function<Session, Object> value) {
            return new KafkaRequestReplyActionBuilder(
                    wrapped.message(Expressions.toStaticValueExpression(key), Expressions.javaFunctionToExpression(value))
            );
        }

        @NonNull
        public KafkaRequestReplyActionBuilder message(@NonNull Function<Session, Object> key, @NonNull Object value) {
            return new KafkaRequestReplyActionBuilder(
                    wrapped.message(Expressions.javaFunctionToExpression(key), Expressions.toStaticValueExpression(value))
            );
        }

        @NonNull
        public KafkaRequestReplyActionBuilder message(@NonNull Object value, @NonNull Map<String, String> headers) {
            return new KafkaRequestReplyActionBuilder(
                    wrapped.message(
                            Expressions.toStaticValueExpression(value),
                            KafkaConverters.stringHeadersToScala(headers)
                    )
            );
        }

        @NonNull
        public KafkaRequestReplyActionBuilder message(
                @NonNull Function<Session, Object> value,
                @NonNull Map<Function<Session, String>, Function<Session, String>> headers
        ) {
            return new KafkaRequestReplyActionBuilder(
                    wrapped.message(
                            javaFunctionToExpression(value),
                            KafkaConverters.expressionHeadersToScala(headers)
                    )
            );
        }

        @NonNull
        public KafkaRequestReplyActionBuilder message(@NonNull Object key, @NonNull Object value, @NonNull Map<String, String> headers) {
            return new KafkaRequestReplyActionBuilder(
                    wrapped.message(
                            Expressions.toStaticValueExpression(key),
                            Expressions.toStaticValueExpression(value),
                            KafkaConverters.stringHeadersToScala(headers)
                    )
            );
        }

        @NonNull
        public KafkaRequestReplyActionBuilder message(
                @NonNull Function<Session, Object> key,
                @NonNull Function<Session, Object> value,
                @NonNull Map<Function<Session, String>, Function<Session, String>> headers
        ) {
            return new KafkaRequestReplyActionBuilder(
                    wrapped.message(
                            javaFunctionToExpression(key),
                            javaFunctionToExpression(value),
                            KafkaConverters.expressionHeadersToScala(headers)
                    )
            );
        }
    }

}
