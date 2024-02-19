package io.github.vdanilau.gatling.javaapi.kafka;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.gatling.commons.validation.Validation;
import io.gatling.core.session.Session;
import io.github.vdanilau.gatling.kafka.request.KafkaDslBuilderBase;
import scala.Function1;

public class Kafka {

    private final KafkaDslBuilderBase wrapped;

    public Kafka(Function1<Session, Validation<String>> name) {
        wrapped = new KafkaDslBuilderBase(name);
    }

    @NonNull
    public KafkaSendActionBuilder.Topic send() {
        return new KafkaSendActionBuilder.Topic(wrapped.send());
    }

    @NonNull
    public KafkaRequestReplyActionBuilder.Topic requestReply() {
        return new KafkaRequestReplyActionBuilder.Topic(wrapped.requestReply());
    }
}
