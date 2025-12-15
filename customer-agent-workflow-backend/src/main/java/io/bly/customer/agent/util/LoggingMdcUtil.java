package io.bly.customer.agent.util;

import org.slf4j.MDC;

import java.util.Map;

/**
 * Utility for managing MDC context (log correlation).
 */
public class LoggingMdcUtil {

    private static final String KEY_PROCESS_INSTANCE_ID = "processInstanceId";
    private static final String KEY_ORDER_ID = "orderId";
    private static final String KEY_TICKET_ID = "ticketId";
    private static final String KEY_AGENT_ID = "agentId";

    private LoggingMdcUtil() {
    }

    public static void putProcessInstanceId(String processInstanceId) {

        if (processInstanceId != null) {
            MDC.put(KEY_PROCESS_INSTANCE_ID, processInstanceId);
        }
    }

    public static void putOrderId(String orderId) {

        if (orderId != null) {
            MDC.put(KEY_ORDER_ID, orderId);
        }
    }

    public static void putTicketId(Long ticketId) {

        if (ticketId != null) {
            MDC.put(KEY_TICKET_ID, ticketId.toString());
        }
    }

    public static void putAgentId(String agentId) {

        if (agentId != null) {
            MDC.put(KEY_AGENT_ID, agentId);
        }
    }

    public static void clear() {
        MDC.remove(KEY_PROCESS_INSTANCE_ID);
        MDC.remove(KEY_ORDER_ID);
        MDC.remove(KEY_TICKET_ID);
        MDC.remove(KEY_AGENT_ID);
    }

    /**
     * Bulk setter from a map of arbitrary MDC keys.
     */
    public static void putAll(Map<String, String> values) {

        if (values == null) {
            return;
        }

        values.forEach((k, v) -> {
            if (v != null) {
                MDC.put(k, v);
            }
        });
    }
}
