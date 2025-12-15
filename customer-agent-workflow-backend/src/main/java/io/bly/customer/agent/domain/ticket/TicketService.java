package io.bly.customer.agent.domain.ticket;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    @Transactional
    public Ticket createTicket(String orderId,
                               String issueType,
                               String description,
                               String processInstanceId) {

        Ticket ticket = Ticket.builder()
                .orderId(orderId)
                .issueType(issueType)
                .description(description)
                .status("OPEN")
                .processInstanceId(processInstanceId)
                .build();

        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByOrderId(String orderId) {
        return ticketRepository.findByOrderId(orderId);
    }

    @Transactional
    public Ticket updateStatus(Long ticketId, String newStatus) {

        Ticket ticket = getTicket(ticketId);
        ticket.setStatus(newStatus);

        return ticketRepository.save(ticket);
    }
}
