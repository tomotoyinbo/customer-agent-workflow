package io.bly.customer.agent.workflow.delegate;

import io.bly.customer.agent.messaging.TicketCreatedEvent;
import io.bly.customer.agent.messaging.TicketEventProducer;
import io.bly.customer.agent.util.LoggingMdcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Sends a "ticket created" notification event using Kafka.
 * Reads required data from process variables populated earlier in the workflow.
 */
@Slf4j
@Component("notifyCustomerDelegate")
@RequiredArgsConstructor
public class NotifyCustomerDelegate implements JavaDelegate {

    private final TicketEventProducer ticketEventProducer;

    @Override
    public void execute(DelegateExecution execution) {

        String processInstanceId = execution.getProcessInstanceId();
        String orderId = (String) execution.getVariable("orderId");
        Long ticketId = (Long) execution.getVariable("ticketId");

        // MDC setup
        LoggingMdcUtil.putProcessInstanceId(processInstanceId);
        LoggingMdcUtil.putOrderId(orderId);
        LoggingMdcUtil.putTicketId(ticketId);

        try {
            String issueType = (String) execution.getVariable("issueType");

            @SuppressWarnings("unchecked")
            Map<String, Object> customerContact =
                    (Map<String, Object>) execution.getVariable("customerContact");

            String customerEmail = customerContact != null ? (String) customerContact.get("email") : null;
            String customerPhone = customerContact != null ? (String) customerContact.get("phone") : null;

            log.debug("Publishing ticket created event issueType={}", issueType);

            TicketCreatedEvent event = TicketCreatedEvent.now(
                    ticketId,
                    issueType,
                    orderId,
                    customerEmail,
                    customerPhone
            );

            ticketEventProducer.publishTicketCreated(event);

            log.info("Published TicketCreatedEvent");

        } catch (Exception ex) {
            log.error("Unexpected error while notifying customer", ex);
            throw ex;
        } finally {
            // MDC cleanup (critical)
            LoggingMdcUtil.clear();
        }
    }
}
