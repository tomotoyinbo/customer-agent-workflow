package io.bly.customer.agent.domain.ticket;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "issue_ticket")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String issueType;    // LOST_ITEM, DAMAGED_ITEM, ADDRESS_CHANGE

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String status;       // OPEN, IN_PROGRESS, RESOLVED, etc.

    @Column(nullable = false)
    private String processInstanceId;

    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
