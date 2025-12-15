package io.bly.customer.agent.workflow.dto;

/**
 * Response for POST /api/issues/start
 */
public record StartIssueResponse(
        String processInstanceId,
        String status) {

    public static StartIssueResponse started(String processInstanceId) {
        return new StartIssueResponse(processInstanceId, "STARTED");
    }
}
