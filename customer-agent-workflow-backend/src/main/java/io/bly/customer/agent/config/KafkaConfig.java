package io.bly.customer.agent.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    /**
     * Generic producer factory that can send any Object as the value.
     * Jackson-based JsonSerializer is used for serialization.
     * <p>
     * You can reuse this for different event types:
     *  - TicketCreatedEvent
     *  - CustomerNotificationEvent
     *  - etc.,
     */
    @Bean
    public ProducerFactory<String, Object> kafkaProducerFactory(
            KafkaProperties kafkaProperties,
            ObjectMapper objectMapper) {

        Map<String, Object> props = kafkaProperties.buildProducerProperties();

        // Explicit serializers
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        JsonSerializer<Object> jsonSerializer = new JsonSerializer<>(objectMapper);
        // If you want to include type info in headers (for polymorphic consumers),
        // set this to true. For single-type-per-topic, false is usually fine.
        jsonSerializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), jsonSerializer);
    }

    /**
     * Generic KafkaTemplate for sending arbitrary event objects.
     * <p>
     * Usage:
     *   kafkaTemplate.send(topic, key, new TicketCreatedEvent(...));
     *   kafkaTemplate.send(topic, key, new SomeOtherEvent(...));
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    /**
     * Topic definition for ticket-created events.
     * Add more @Bean NewTopic methods for other topics as needed.
     */
    @Bean
    public NewTopic ticketCreatedTopic(
            @Value("${app.kafka.topics.ticket-created:ticket.created}") String topicName) {

        return TopicBuilder.name(topicName)
                .partitions(1)
                .replicas(1)
                .compact()  // or drop this and configure cleanup.policy via config() if prefered
                .build();
    }

    // Example for another topic you might add later:
    // @Bean
    // public NewTopic customerNotificationTopic(
    //         @Value("${app.kafka.topics.customer-notification:customer.notification}") String topicName) {
    //     return TopicBuilder.name(topicName)
    //             .partitions(1)
    //             .replicas(1)
    //             .build();
    // }
}
