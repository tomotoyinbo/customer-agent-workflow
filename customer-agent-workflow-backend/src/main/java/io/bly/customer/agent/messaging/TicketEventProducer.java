package io.bly.customer.agent.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Producer for ticket-related events.
 * Wraps KafkaTemplate to keep Kafka details out of the domain & delegates.
 */
@Component
@RequiredArgsConstructor
public class TicketEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.ticket-created:ticket.created}")
    private String ticketCreatedTopic;

    public void publishTicketCreated(TicketCreatedEvent event) {

        String key = event.ticketId() != null
                ? event.ticketId().toString()
                : event.orderId();

        kafkaTemplate.send(ticketCreatedTopic, key, event);
    }
}
