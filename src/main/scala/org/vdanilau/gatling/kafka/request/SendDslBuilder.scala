package org.vdanilau.gatling.kafka.request

import com.softwaremill.quicklens.ModifyPimp
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression
import org.vdanilau.gatling.kafka.action.{RequestReplyBuilder, SendBuilder}
import org.vdanilau.gatling.kafka.protocol.KafkaMessageMatcher

final class KafkaDslBuilderBase(requestName: Expression[String]) {
  def fireAndForget: SendDslBuilder.Topic = new SendDslBuilder.Topic(requestName)
  
  def requestReply: RequestReplyDslBuilder.Topic = new RequestReplyDslBuilder.Topic(requestName)
}

object SendDslBuilder {
  final class Topic(requestName: Expression[String]) {
    def destination(destination: Expression[String]): Message = topic(destination)
    
    def topic(topic: Expression[String]): Message = new Message(requestName, topic)
  }
  
  final class Message(requestName: Expression[String], topic: Expression[String]) {
    def payload(key: Expression[Any], value: Expression[Any]): SendDslBuilder = builder(Some(key), value, None)
    
    def payload(key: Expression[Any], value: Expression[Any], headers: Map[Expression[String], Expression[String]]): SendDslBuilder = builder(Some(key), value, Some(headers))
    
    def payload(value: Expression[Any]): SendDslBuilder = builder(None, value, None)
    
    def payload(value: Expression[Any], headers: Map[Expression[String], Expression[String]]): SendDslBuilder = builder(None, value, Some(headers))
    
    def message(key: Expression[Any], value: Expression[Any]): SendDslBuilder = payload(key, value)
    
    def message(key: Expression[Any], value: Expression[Any], headers: Map[Expression[String], Expression[String]]): SendDslBuilder = payload(key, value, headers)
    
    def message(value: Expression[Any]): SendDslBuilder = payload(value)
    
    def message(value: Expression[Any], headers: Map[Expression[String], Expression[String]]): SendDslBuilder = payload(value, headers)
    
    private def builder(
      key: Option[Expression[Any]],
      value: Expression[Any],
      headers: Option[Map[Expression[String], Expression[String]]]
    ): SendDslBuilder = SendDslBuilder(KafkaAttributes(requestName, topic, key, value, headers, None, None), new SendBuilder(_))
  }
}

final case class SendDslBuilder(
  attributes: KafkaAttributes,
  factory: KafkaAttributes => ActionBuilder
) {
  
  def header(key: Expression[String], value: Expression[String]): SendDslBuilder = attributes.headers match {
    case Some(headers) =>
      this.modify(_.attributes.headers).setTo(Some(headers + (key -> value)))
    case None =>
      val headers = Some(Map(key -> value))
      this.modify(_.attributes.headers).setTo(headers)
  }
  
  def headers(headers: Map[Expression[String], Expression[String]]): Unit = {
    this.modify(_.attributes.headers).setTo(Some(headers))
  }
  
  
  def build: ActionBuilder = factory(attributes)
}

object RequestReplyDslBuilder {
  final class Topic(requestName: Expression[String]) {
    def destination(destination: Expression[String]): ReplyTopic = topic(destination)
    
    def topic(topic: Expression[String]): ReplyTopic = new ReplyTopic(requestName, topic)
  }
  
  final class ReplyTopic(requestName: Expression[String], topic: Expression[String]) {
    def replyTopic(replyTopic: Expression[String]): MessageMatcher = new MessageMatcher(requestName, topic, replyTopic)
  }
  
  final class MessageMatcher(
    requestName: Expression[String],
    topic: Expression[String],
    replyTopic: Expression[String]) {
    def messageMatcher(messageMatcher: KafkaMessageMatcher[?, ?, ?, ?]): Message = new Message(requestName, topic, replyTopic, messageMatcher.asInstanceOf[KafkaMessageMatcher[Any, Any, Any, Any]])
  }
  
  final class Message(
    requestName: Expression[String],
    topic: Expression[String],
    replyTopic: Expression[String],
    messageMatcher: KafkaMessageMatcher[Any, Any, Any, Any]
  ) {
    def payload(key: Expression[Any], value: Expression[Any]): RequestReplyDslBuilder = builder(Some(key), value, None)
    
    def payload(key: Expression[Any], value: Expression[Any], headers: Map[Expression[String], Expression[String]]): RequestReplyDslBuilder = builder(Some(key), value, Some(headers))
    
    def payload(value: Expression[Any]): RequestReplyDslBuilder = builder(None, value, None)
    
    def payload(value: Expression[Any], headers: Map[Expression[String], Expression[String]]): RequestReplyDslBuilder = builder(None, value, Some(headers))
    
    def message(key: Expression[Any], value: Expression[Any]): RequestReplyDslBuilder = payload(key, value)
    
    def message(key: Expression[Any], value: Expression[Any], headers: Map[Expression[String], Expression[String]]): RequestReplyDslBuilder = payload(key, value, headers)
    
    def message(value: Expression[Any]): RequestReplyDslBuilder = payload(value)
    
    def message(value: Expression[Any], headers: Map[Expression[String], Expression[String]]): RequestReplyDslBuilder = payload(value, headers)
    
    private def builder(
      key: Option[Expression[Any]],
      value: Expression[Any],
      headers: Option[Map[Expression[String], Expression[String]]]
    ): RequestReplyDslBuilder = RequestReplyDslBuilder(KafkaAttributes(requestName, topic, key, value, headers, Some(replyTopic), Some(messageMatcher)), new RequestReplyBuilder(_))
  }
}

final case class RequestReplyDslBuilder(
  attributes: KafkaAttributes,
  factory: KafkaAttributes => ActionBuilder
) {
  def header(key: Expression[String], value: Expression[String]): RequestReplyDslBuilder = attributes.headers match {
    case Some(headers) =>
      this.modify(_.attributes.headers).setTo(Some(headers + (key -> value)))
    case None =>
      val headers = Some(Map(key -> value))
      this.modify(_.attributes.headers).setTo(headers)
  }
  
  def headers(headers: Map[Expression[String], Expression[String]]): RequestReplyDslBuilder = {
    this.modify(_.attributes.headers).setTo(Some(headers))
  }
  
  
  def build: ActionBuilder = factory(attributes)
}
