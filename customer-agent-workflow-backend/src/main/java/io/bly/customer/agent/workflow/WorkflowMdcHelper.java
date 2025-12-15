package io.bly.customer.agent.workflow;

import io.bly.customer.agent.util.LoggingMdcUtil;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Helper to apply MDC correlation keys for workflow-related HTTP endpoints.
 * <p>
 * Populates MDC with:
 *  - processInstanceId (from Task)
 *  - orderId (from ProcessInstance businessKey)
 * <p>
 * Always clears MDC after the action.
 */
@Component
@RequiredArgsConstructor
public class WorkflowMdcHelper {

    private final TaskService taskService;

    private final RuntimeService runtimeService;

    /**
     * Executes an action with MDC populated using the task's processInstanceId and businessKey (orderId).
     *
     * @param taskId Camunda task id
     * @param action runnable action to execute
     */
    public void runWithTaskMdc(String taskId, Runnable action) {

        try {
            resolveAndPutTaskMdc(taskId);
            action.run();
        } finally {
            LoggingMdcUtil.clear();
        }
    }

    /**
     * Executes a Supplier action with MDC populated using the task's processInstanceId and businessKey (orderId),
     * and returns a value.
     *
     * @param taskId Camunda task id
     * @param action supplier to execute
     * @return action result
     */
    public <T> T supplyWithTaskMdc(String taskId, Supplier<T> action) {
        try {
            resolveAndPutTaskMdc(taskId);
            return action.get();
        } finally {
            LoggingMdcUtil.clear();
        }
    }

    /**
     * Resolve Task -> processInstanceId, then ProcessInstance -> businessKey,
     * and put values into MDC.
     */
    private void resolveAndPutTaskMdc(String taskId) {

        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        String processInstanceId = task.getProcessInstanceId();
        LoggingMdcUtil.putProcessInstanceId(processInstanceId);

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (processInstance != null && processInstance.getBusinessKey() != null) {
            // businessKey == orderId in this design
            LoggingMdcUtil.putOrderId(processInstance.getBusinessKey());
        }
    }

    /**
     * Optional faster version (recommended) if you always store orderId as a process variable.
     * You can swap this in later to avoid querying ProcessInstance for businessKey.
     */
    @SuppressWarnings("unused")
    private void resolveAndPutTaskMdcUsingVariable(String taskId) {

        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        String processInstanceId = task.getProcessInstanceId();
        LoggingMdcUtil.putProcessInstanceId(processInstanceId);

        String orderId = (String) runtimeService.getVariable(processInstanceId, "orderId");

        LoggingMdcUtil.putOrderId(orderId);
    }
}
