package io.bly.customer.agent.messaging;

import java.time.Instant;

/**
 * Event published when a ticket is created.
 * Serialized as JSON onto Kafka.
 */
public record TicketCreatedEvent(
        Long ticketId,
        String issueType,
        String orderId,
        String customerEmail,
        String customerPhone,
        Instant occurredAt) {

    public static TicketCreatedEvent now(Long ticketId,
                                         String issueType,
                                         String orderId,
                                         String customerEmail,
                                         String customerPhone) {

        return new TicketCreatedEvent(
                ticketId,
                issueType,
                orderId,
                customerEmail,
                customerPhone,
                Instant.now()
        );
    }
}
