package com.klab.services.backarchetype.messaging;

import com.klab.core.starter.audit.model.avro.AvroAudit;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

/**
 * Producer component for sending audit events to Kafka.
 * <b>Class</b>: AuditProducer
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */
@Component
public class AuditProducer {

  private static final Logger LOGGER = Logger.getLogger(AuditProducer.class);

  private final KafkaSender<String, AvroAudit> producer;
  private final String topic;

  /**
   * Constructor for AuditProducer.
   *
   * @param producer the Kafka sender for AvroAudit messages
   * @param topic    the topic to send audit messages to
   */
  public AuditProducer(
      @Name("producerAudit") KafkaSender<String, AvroAudit> producer,
      @Value("${spring.kafka.producer.topic}") String topic) {
    this.producer = producer;
    this.topic = topic;
    LOGGER.info("Producer audit topic: " + topic);
  }

  /**
   * Sends an audit event to the configured Kafka topic.
   *
   * @param avroAudit the audit event to send
   * @return Mono that completes when the message is sent
   */
  public Mono<Void> send(AvroAudit avroAudit) {
    ProducerRecord<String, AvroAudit> producerRecord =
        new ProducerRecord<>(topic, avroAudit);

    SenderRecord<String, AvroAudit, String> senderRecord =
        SenderRecord.create(producerRecord, avroAudit.getEventId());

    LOGGER.debug("Sending message to topic: '%s'".formatted(topic));

    return producer.send(Mono.just(senderRecord))
        .doOnNext(result -> LOGGER.infof(
            "Message sent to topic '%s', partition %d, offset %d",
            result.recordMetadata().topic(),
            result.recordMetadata().partition(),
            result.recordMetadata().offset()))
        .doOnError(error -> LOGGER.errorf("Error sending message to topic '%s': %s",
            topic, error.getMessage()))
        .then();
  }

}
