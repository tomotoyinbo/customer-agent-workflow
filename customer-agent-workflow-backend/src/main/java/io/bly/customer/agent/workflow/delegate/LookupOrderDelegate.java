package io.bly.customer.agent.workflow.delegate;

import io.bly.customer.agent.domain.order.OrderData;
import io.bly.customer.agent.domain.order.OrderService;
import io.bly.customer.agent.util.LoggingMdcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Looks up an order by orderId and writes order/shipping/contact details
 * as process variables for the workflow.
 * <p>
 * Important: Camunda process variables should be kept simple and serializable.
 * Using Map<String, Object> variables avoids issues with Java serialization.
 */
@Slf4j
@Component("lookupOrderDelegate")
@RequiredArgsConstructor
public class LookupOrderDelegate implements JavaDelegate {

    private final OrderService orderService;

    @Override
    public void execute(DelegateExecution execution) {

        String processInstanceId = execution.getProcessInstanceId();
        String orderId = (String) execution.getVariable("orderId");

        // ---- MDC setup (must be first) ----
        LoggingMdcUtil.putProcessInstanceId(processInstanceId);
        LoggingMdcUtil.putOrderId(orderId);

        try {
            if (orderId == null || orderId.isBlank()) {

                execution.setVariable("orderFound", false);
                execution.setVariable(
                        "orderLookupError",
                        "Missing required process variable: orderId"
                );

                log.warn("Order lookup skipped: orderId is missing or blank");

                return;
            }

            log.debug("Looking up order");

            Optional<OrderData> orderOpt = orderService.findByOrderId(orderId);

            if (orderOpt.isEmpty()) {
                execution.setVariable("orderFound", false);
                execution.setVariable(
                        "orderLookupError",
                        "Order not found for orderId=" + orderId
                );

                // Defensive cleanup of downstream variables
                execution.removeVariable("productDetails");
                execution.removeVariable("shippingDetails");
                execution.removeVariable("customerContact");

                log.info("Order not found");
                return;
            }

            OrderData order = orderOpt.get();

            execution.setVariable("orderFound", true);
            execution.setVariable("productDetails", order.productDetailsAsMap());
            execution.setVariable("shippingDetails", order.shippingDetailsAsMap());
            execution.setVariable("customerContact", order.customerContactAsMap());

            // Optional convenience variables
            execution.setVariable("trackingNumber", order.getTrackingNumber());
            execution.setVariable("shippingMethod", order.getShippingMethod());

            execution.removeVariable("orderLookupError");

            log.info(
                    "Order found productSku={} trackingNumber={}",
                    order.getProductSku(),
                    order.getTrackingNumber()
            );

        } catch (Exception ex) {
            // Catch-and-rethrow so Camunda can handle retries/incidents
            log.error("Unexpected error during order lookup", ex);
            throw ex;
        } finally {
            // ---- MDC cleanup (CRITICAL) ----
            LoggingMdcUtil.clear();
        }
    }
}
