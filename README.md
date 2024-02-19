# Introduction

Kafka support for Gatling

# Prerequisites
Currently, plugin requires JDK 17 or higher or Scala 2.13, and Gatling 3.9

# Dependency
### Maven
```xml
<dependency>
    <groupId>package</groupId>
    <artifactId>artifact</artifactId>
    <version>version</version>
</dependency>
```
### SBT
```scala
libraryDependencies ++= "package" % "artifact" % "version"
```
# Imports

### Java

```Java
import io.github.vdanilau.gatling.javaapi.kafka.*;
import static io.github.vdanilau.gatling.javaapi.kafka.KafkaDsl.*;
```

### Scala
```scala
import io.github.vdanilau.gatling.Predef._
```

# KafkaProtocol
In order to create KafkaProtocol, use the `kafka` object

The first mandatory step is to create `producerProperties`
### Java

```java
import io.github.vdanilau.gatling.javaapi.kafka.*;
import static io.github.vdanilau.gatling.javaapi.kafka.KafkaDsl.*;

/* ... */

KafkaProtocolBuilder kafkaProtocol = kafka
        .producerProperties(
                Map.ofEntries(
                        Map.entry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9091"),
                        Map.entry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()),
                        Map.entry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName())
                )
        );
```
### Scala
```scala
import io.github.vdanilau.gatling.Predef._

/* ... */

val protocol: KafkaProtocolBuilder = kafka
  .producerProperties(
    Map(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> classTag[StringSerializer].runtimeClass.getName,
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> classTag[KafkaAvroSerializer].runtimeClass.getName
    )
  )
```
## Other KafkaProtocol options
### Java

```java
import java.time.Duration;
import io.github.vdanilau.gatling.javaapi.kafka.*;
import static io.github.vdanilau.gatling.javaapi.kafka.KafkaDsl.*;

KafkaProtocolBuilder kafkaProtocol = kafka
        .producerProperties(producerProperties)
        /* KafkaConsumer properties that would be used by the request&reply protocol to read reply topic */
        .consumerProperties(
                Map.ofEntries(
                        Map.entry(ConsumerConfig.GROUP_ID_CONFIG, "kafka-performance-test"),
                        Map.entry(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"),
                        Map.entry(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()),
                        Map.entry(ConsumerConfig.VALUE_DEERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName())
                )
        )
        /* Reply timeout */
        .replyTimeout(Duration.ofSeconds(10));
```
### Scala
```scala
import io.github.vdanilau.gatling.Predef._

/* ... */

kafka
  .producerProperties(producerProperties)
  /* KafkaConsumer properties that would be used by the request&reply protocol to read reply topic */
  .consumerProperties(
      Map(
        ConsumerConfig.GROUP_ID_CONFIG -> "gatling-performance-test",
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classTag[StringDeserializer].runtimeClass.getName,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classTag[KafkaAvroDeserializer].runtimeClass.getName
      )
    )
  /* Reply timeout */
  .replyTimeout(FiniteDuration(10, SECONDS))
```

# Kafka Request
In order to create Kafka request, use `kafka("request-name)"` method

## Request Type
Currently, `requestReply` and `send` (fire & forget) requests are supported

# Topic
Define the target topic with `topic("topic-name")` method
For `requestReply` request type, `replyTopic("reply-topic-name")` method could be used to specify the reply topic

# Message

Currently, the following method signatures are available to specify a message to send
* `message(value)`
* `message(key, value)`
* `message(key, value, headers)`
* `message(value, headers)`

See below for a few examples:

```java
import java.util.Map;
import io.github.vdanilau.gatling.javaapi.kafka.*;
import static io.github.vdanilau.gatling.javaapi.kafka.KafkaDsl.*;

/* ... */

void example() {
    /* with static string message with only value specified */
    kafka("kafka-request-name")
            .send()
            .topic("destination-topic")
            .message("value");
    /* with static string message with both key and value specified */
    kafka("kafka-request-name")
            .send()
            .topic("destination-topic")
            .message("key", "value");
    /* with static string message with key, value and headers specified */
    kafka("kafka-request-name")
            .send()
            .topic("destination-topic")
            .message("key", "value", Map.ofEntries(Map.entry("key", "value")));
    /* with static object message */
    kafka("kafka-request-name")
            .send()
            .topic("destination-topic")
            .message("key", new Person("fist-name", "last-name"));
    /* with a Gatling EL message */
    kafka("kafka-request-name")
            .send()
            .topic("destination-topic")
            .message("#{key}", "#{value}");
    /* with function message */
    kafka("kafka-request-name")
            .send()
            .topic("destination-topic")
            .message(session -> session.getString("key"), session -> session.get("value"));

}
```
### Scala
```scala
import io.github.vdanilau.gatling.Predef._

/* ... */

/* with static string message with only value specified */
kafka("kafka-request-name")
  .send
  .topic("destination-topic")
  .message("value")
/* with static string message with both key and value specified */
kafka("kafka-request-name")
  .send
  .topic("destination-topic")
  .message("key", "value")
/* with static string message with key, value and headers specified */
kafka("kafka-request-name")
  .send()
  .topic("destination-topic")
  .message("key", "value", Map("key" -> "value"))
/* with static object message */
kafka("kafka-request-name")
  .send()
  .topic("destination-topic")
  .message("key", Person("fist-name", "last-name"))
/* with a Gatling EL message */
kafka("kafka-request-name")
  .send()
  .topic("destination-topic")
  .message("#{key}", "#{value}")
/* with function message */
kafka("kafka-request-name")
  .send()
  .topic("destination-topic")
  .message(
    { session => session.attributes("key") },
    { session => session.attributes("value") }
  )
```

# Request&Reply requests
## Overview
Plugins supports a request&reply pattern to wait for sent messages to be replied to a second topic

For request&reply pattern a `messageMatcher` need to be provided in order to match outgoing and incoming messages

Matching of message is implemented via `io.github.vdanilau.gatling.kafka.protocol.KafkaMessageMatcher` interface

Plugins provides default `KeyMessageMatcher` implementation. However, you can create your custom implementation.

See below for an example:
### Java

```java
import io.github.vdanilau.gatling.javaapi.kafka.*;
import static io.github.vdanilau.gatling.javaapi.kafka.KafkaDsl.*;

/* ... */

void example() {
    kafka("request/reply")
            .requestReply()
            .topic("destination-topic")
            .replyTopic("reply-topic")
            /* matching outgoing and incoming message by key */
            .matchByKey()
            /* or alternatively the custom implementation of KafkaMessageMatcher interface */
            .messageMatcher(new CustomKafkaMessageMatcher())
            .message("key", "value");
}
```

### Scala
```scala
import io.github.vdanilau.gatling.Predef._

/* ... */

kafka("request/reply")
  .requestReply
  .topic("destination-topic")
  .replyTopic("reply-topic")
  /* matching outgoing and incoming message by key */
  .matchByKey
  /* or alternatively the custom implementation of KafkaMessageMatcher interface */
  .messageMatcher(CustomKafkaMessageMatcher())
  .message("key", "value")
```

## Kafka Check
Request&reply requests provide support for checks.

Currently, the following checks are supported:
* simpleCheck
* consumerRecordCheck
* keyCheck
* valueCheck
* headersCheck
* headerCheck

`consumerRecordCheck`, `keyCheck`, `valueCheck` checks are trying to cast received object to specific types assumed from check parameter types. Otherwise, checks are going to fail.

See below for an example:
### Java

```java
import java.util.Map;
import io.github.vdanilau.gatling.javaapi.kafka.*;
import static io.github.vdanilau.gatling.javaapi.kafka.KafkaDsl.*;

/* ... */

void example() {
    Function<ConsumerRecord<Any, Any>, Boolean> simpleCheck = record -> {
        /* ... check logic ... */
        return true;
    };

    Function<ConsumerRecord<String, String>, Boolean> consumerRecordCheck = record -> {
        /* ... check logic ... */
        return true;
    };

    Function<String, Boolean> keyCheck = key -> {
        /* ... check logic ... */
        return true;
    };

    Function<String, Boolean> valueCheck = value -> {
        /* ... check logic ... */
        return true;
    };

    kafka("request/reply")
            .requestReply()
            .topic("destination-topic")
            .replyTopic("reply-topic")
            .matchByKey()
            .message("key", "value")
            /* simple check without casting to a specific type */
            .simpleCheck(simpleCheck)
            /* ConsumerRecord check */
            .consumerRecordCheck(consumerRecordCheck)
            /* ConsumerRecord's key check */
            .keyCheck(keyCheck)
            /* ConsumerRecord's value check */
            .valueCheck(valueCheck)
            /* ConsumerRecord's headers check */
            .headersCheck(headers -> true)
            /* ConsumerRecord's header check */
            .headerCheck(header -> true);
}
```
### Scala
```scala
import io.github.vdanilau.gatling.Predef._

val simpleCheck: ConsumerRecord[Any, Any] => Boolean = record => {
  /* ... check logic ... */
  true
}

val consumerRecordCheck: ConsumerRecord[String, String] => Boolean = record -> {
  /* ... check logic ... */
  true
}

val keyCheck: String => Boolean = key -> {
  /* ... check logic ... */
  true
}

val valueCheck: String => Boolean = value -> {
  /* ... check logic ... */
  true
}

kafka("request/reply")
  .requestReply
  .topic("destination-topic")
  .replyTopic("reply-topic")
  .matchByKey
  .message("key", "value")
  /* simple check without casting to a specific type */
  .simpleCheck(simpleCheck)
  /* ConsumerRecord check */
  .check(consumerRecordCheck(consumerRecordCheck))
  /* ConsumerRecord's key check */
  .check(keyCheck(keyCheck))
  /* ConsumerRecord's value check */
  .check(valueCheck(valueCheck))
  /* ConsumerRecord's header check */
  .check(headerCheck("key", {header => true}))
  /* ConsumerRecord's headers check */
  .check(headersCheck { headers => true })
```

# Other options
Additionally, headers could be specified separately using `header(key, value)` method

### Java
```java
import io.github.vdanilau.gatling.javaapi.kafka.*;
import static io.github.vdanilau.gatling.javaapi.kafka.KafkaDsl.*;

/* ... */

void example() {
    kafka("request/reply")
            .requestReply()
            .topic("destination-topic")
            .replyTopic("reply-topic")
            .matchByKey()
            .message("key", "value")
            .header("header-key", "header");
}
```
### Scala
```scala
import io.github.vdanilau.gatling.Predef._

/* ... */

kafka("request/reply")
  .requestReply
  .topic("destination-topic")
  .replyTopic("reply-topic")
  .matchByKey
  .message("key", "value")
  .header("header-key", "header")
```
