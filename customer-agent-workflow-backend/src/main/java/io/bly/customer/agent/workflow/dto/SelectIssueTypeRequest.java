package io.bly.customer.agent.workflow.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for POST /api/issues/tasks/{taskId}/select-type
 */
public record SelectIssueTypeRequest(
        @NotBlank String issueType) {}
