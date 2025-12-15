package io.bly.customer.agent.workflow.dto;

/**
 * DTO representing a Camunda user task exposed to the UI.
 */
public record TaskDto(
        String taskId,
        String name,
        String taskDefinitionKey,
        String processInstanceId,
        String businessKey) {

}
