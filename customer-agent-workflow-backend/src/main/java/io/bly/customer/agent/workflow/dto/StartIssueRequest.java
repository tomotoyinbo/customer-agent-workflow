package io.bly.customer.agent.workflow.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for POST /api/issues/start
 */
public record StartIssueRequest(
        @NotBlank String orderId,
        @NotBlank String agentId) {}
