package com.antmendoza.temporal.usertask.advancedimplementation.tasks;

import com.antmendoza.temporal.usertask.advancedimplementation.workflow.*;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.CancellationScope;
import io.temporal.workflow.Workflow;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;

public class WorkflowTaskManagerImpl implements WorkflowTaskManager {
  private final Logger logger = Workflow.getLogger(WorkflowTaskManagerImpl.class.getName());

  private final TasksList taskListService = new TasksList();

  private final Map<String, CancellationScope> timers = new HashMap();

  @Override
  public void run(TasksList taskList) {

    this.taskListService.addAll(taskList);

    while (true) {

      // Wait until there are pending task to process
      Workflow.await(this.taskListService::hasUnprocessedTasks);

      final Task unprocessedTask = this.taskListService.getNextUnprocessedTask();
      logger.info("Processing task {}", unprocessedTask);

      scheduleTimerIfDeadline(unprocessedTask);

      notifyAssignedToHasChanged(unprocessedTask);

      if (unprocessedTask.isClosed()) {
        notifyClient(unprocessedTask);
      }
    }

    // TODO
    // Workflow.continueAsNew();

  }

  @Override
  public void addTask(Task task) {
    this.taskListService.add(task);
  }

  @Override
  public void validateCompleteTask(CompleteTaskRequest changeTaskRequest) {

    final String taskId = changeTaskRequest.taskId();
    final TaskState completed = TaskState.Completed;
    if (!taskListService.getTask(taskId).canTaskTransitionToState(completed)) {
      final TaskState taskState = taskListService.getTask(taskId).getTaskState();
      throw new RuntimeException(
          "Task with id ["
              + taskId
              + "], "
              + "with state ["
              + taskState
              + "], can not transition to "
              + completed);
    }
  }

  @Override
  public void completeTask(CompleteTaskRequest changeTaskRequest) {
    logger.info("Completing task {}", changeTaskRequest);
    taskListService.completeTask(changeTaskRequest);
  }

  @Override
  public List<Task> getPendingTasks(final TaskFilter taskFilter) {
    return taskListService.getTasks(taskFilter);
  }

  private void notifyClient(final Task task) {
    try {

      logger.debug("task: " + task);
      final String taskId = task.getId();
      final String externalWorkflowId = new TaskToken(taskId).getWorkflowId();

      logger.debug("externalWorkflowId: " + externalWorkflowId);

      final TaskClient taskClient =
          Workflow.newExternalWorkflowStub(TaskClient.class, externalWorkflowId);

      if (task.getTaskState().equals(TaskState.Completed)) {

        // cancel timer
        if (timers.containsKey(taskId)) {
          timers.get(taskId).cancel();
          timers.remove(taskId);
        }

        taskClient.completeTaskByToken(taskId, task.getResult());
      }
      if (task.getTaskState().equals(TaskState.TimeOut)) {
        taskClient.timeOutTaskByToken(taskId);
      }

    } catch (ApplicationFailure e) {
      throw e;
    }
  }

  private void notifyAssignedToHasChanged(final Task task) {
    Task previousTask = task.getPreviousState();
    logger.info("Processing previousTask {}", previousTask);
    // notify the user...
    if (previousTask != null
        && !Objects.equals(task.getAssignedTo(), previousTask.getAssignedTo())) {
      // Notify use task.getAssignedTo()
    }
  }

  private void scheduleTimerIfDeadline(final Task task) {
    if (task.getDeadline() != null && !timers.containsKey(task.getId())) {

      CancellationScope cancelableScope =
          Workflow.newCancellationScope(
              () -> {
                Workflow.newTimer(task.getDeadline())
                    .thenApply(
                        t -> {
                          taskListService.timeoutTask(task);
                          return null;
                        });
              });

      cancelableScope.run();
      timers.put(task.getId(), cancelableScope);
    }
  }
}
