package io.bly.customer.agent.workflow.delegate;

import io.bly.customer.agent.domain.ticket.Ticket;
import io.bly.customer.agent.domain.ticket.TicketService;
import io.bly.customer.agent.util.LoggingMdcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Persists a ticket using domain-level TicketService.
 * Keeps Camunda logic separate from domain logic.
 */
@Slf4j
@Component("persistTicketDelegate")
@RequiredArgsConstructor
public class PersistTicketDelegate implements JavaDelegate {

    private final TicketService ticketService;

    @Override
    public void execute(DelegateExecution execution) {

        String processInstanceId = execution.getProcessInstanceId();
        String orderId = (String) execution.getVariable("orderId");
        String issueType = (String) execution.getVariable("issueType");

        // MDC setup
        LoggingMdcUtil.putProcessInstanceId(processInstanceId);
        LoggingMdcUtil.putOrderId(orderId);

        try {
            String description = (String) execution.getVariable("issueDescription");

            log.debug("Persisting ticket issueType={}", issueType);

            Ticket saved = ticketService.createTicket(
                    orderId,
                    issueType,
                    description,
                    processInstanceId
            );

            // Add ticketId to MDC once created (for later log lines)
            LoggingMdcUtil.putTicketId(saved.getId());

            execution.setVariable("ticketId", saved.getId());

            log.info("Ticket created status={} issueType={}", saved.getStatus(), saved.getIssueType());

        } catch (Exception ex) {
            log.error("Unexpected error while persisting ticket", ex);
            throw ex;
        } finally {
            // MDC cleanup (critical)
            LoggingMdcUtil.clear();
        }
    }
}
