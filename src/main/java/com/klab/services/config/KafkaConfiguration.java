package com.klab.services.config;

import com.klab.core.starter.audit.model.avro.AvroAudit;
import java.util.HashMap;
import java.util.Map;
import org.jboss.logging.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

/**
 * Configuration for Kafka-related beans.
 * <b>Class</b>: KafkaConfiguration
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */
@Configuration
public class KafkaConfiguration {
  private static final Logger LOGGER = Logger.getLogger(KafkaConfiguration.class);

  /**
   * Loads general kafka properties from application configuration.
   *
   * @return Map containing kafka configuration properties
   */
  @Bean
  @ConfigurationProperties(prefix = "spring.kafka.properties")
  public Map<String, Object> kafkaProperties() {
    return new HashMap<>();
  }

  /**
   * Loads producer properties from application configuration.
   *
   * @return Map containing producer configuration properties
   */
  @Bean
  @ConfigurationProperties(prefix = "spring.kafka.producer.properties")
  public Map<String, Object> producerProperties() {
    return new HashMap<>();
  }

  /**
   * Creates a reactive KafkaSender for AvroAudit messages.
   *
   * @param kafkaProperties    general kafka configuration properties
   * @param producerProperties the producer configuration properties
   * @return KafkaSender configured for AvroAudit
   */
  @Bean(name = "producerAudit")
  public KafkaSender<String, AvroAudit> producer(
      Map<String, Object> kafkaProperties,
      Map<String, Object> producerProperties) {

    LOGGER.info("=== Kafka Configuration Debug ===");
    LOGGER.info("kafkaProperties: " + kafkaProperties);
    LOGGER.info("producerProperties: " + producerProperties);

    Map<String, Object> props = new HashMap<>();
    props.putAll(kafkaProperties);
    props.putAll(producerProperties);

    LOGGER.info("Merged props: " + props);

    Map<String, Object> properties = new HashMap<>();
    properties.put("bootstrap.servers", "localhost:29092");
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
    properties.put("schema.registry.url", "http://localhost:8081");
    properties.put("security.protocol", "PLAINTEXT");
    properties.put("auto.register.schemas", true);
    properties.put("use.latest.version", true);
    properties.put("specific.avro.writer", true);
    // Specific Avro serializer settings
    properties.put("value.subject.name.strategy",
        "io.confluent.kafka.serializers.subject.TopicNameStrategy");

    LOGGER.info("Hardcoded properties: " + properties);

    SenderOptions<String, AvroAudit> senderOptions = SenderOptions.create(properties);
    return KafkaSender.create(senderOptions);
  }

}

