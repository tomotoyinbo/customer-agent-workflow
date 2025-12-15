package io.bly.customer.agent.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for POST /api/issues/tasks/{taskId}/details
 */
public record IssueDetailsRequest(
        @NotBlank
        @Size(max = 2000)
        String description) {
}
