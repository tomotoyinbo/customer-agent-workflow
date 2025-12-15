package io.bly.customer.agent.workflow.controller;

import io.bly.customer.agent.util.LoggingMdcUtil;
import io.bly.customer.agent.workflow.WorkflowMdcHelper;
import io.bly.customer.agent.workflow.dto.*;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${app.api.base-path}/issues")
@RequiredArgsConstructor
public class IssueWorkflowController {

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final WorkflowMdcHelper workflowMdcHelper;

    @PostMapping("/start")
    public StartIssueResponse startIssue(@RequestBody @Validated StartIssueRequest request) {

        LoggingMdcUtil.putAgentId(request.agentId());
        LoggingMdcUtil.putOrderId(request.orderId());

        try {
            Map<String, Object> vars = new HashMap<>();
            vars.put("orderId", request.orderId());
            vars.put("agentId", request.agentId());

            ProcessInstance pi = runtimeService.startProcessInstanceByKey(
                    "customer_issue_flow",
                    request.orderId(),  // businessKey
                    vars
            );

            // add processInstanceId once we have it
            LoggingMdcUtil.putProcessInstanceId(pi.getProcessInstanceId());

            return StartIssueResponse.started(pi.getProcessInstanceId());

        } finally {
            LoggingMdcUtil.clear();
        }
    }

    @GetMapping("/tasks")
    public List<TaskDto> listTasks(@RequestParam String agentId) {

        LoggingMdcUtil.putAgentId(agentId);

        try {
            List<Task> tasks = taskService.createTaskQuery()
                    .taskCandidateGroup("AGENTS")
                    .list();

            if (tasks.isEmpty()) {
                return List.of();
            }

            Set<String> processInstanceIds = tasks.stream()
                    .map(Task::getProcessInstanceId)
                    .collect(Collectors.toSet());

            Map<String, String> businessKeysByPiId = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceIds(processInstanceIds)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(
                            ProcessInstance::getId,
                            ProcessInstance::getBusinessKey
                    ));

            return tasks.stream()
                    .map(task -> new TaskDto(
                            task.getId(),
                            task.getName(),
                            task.getTaskDefinitionKey(),
                            task.getProcessInstanceId(),
                            businessKeysByPiId.get(task.getProcessInstanceId())
                    ))
                    .toList();

        } finally {
            LoggingMdcUtil.clear();
        }
    }

    @PostMapping("/tasks/{taskId}/select-type")
    public void selectIssueType(@PathVariable String taskId,
                                @RequestBody @Validated SelectIssueTypeRequest request) {

        workflowMdcHelper.runWithTaskMdc(taskId, () ->
                taskService.complete(taskId, Map.of("issueType", request.issueType()))
        );
    }

    @PostMapping("/tasks/{taskId}/details")
    public void provideDetails(@PathVariable String taskId,
                               @RequestBody @Validated IssueDetailsRequest request) {

        workflowMdcHelper.runWithTaskMdc(taskId, () ->
                taskService.complete(taskId, Map.of("issueDescription", request.description()))
        );
    }
}
