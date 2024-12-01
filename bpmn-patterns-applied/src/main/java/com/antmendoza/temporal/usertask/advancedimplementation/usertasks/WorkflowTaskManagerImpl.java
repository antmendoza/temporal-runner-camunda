package com.antmendoza.temporal.usertask.advancedimplementation.usertasks;

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

  private final UserTasksList taskListService = new UserTasksList();

  private final Map<String, CancellationScope> timers = new HashMap();

  @Override
  public void run(UserTasksList taskList) {

    this.taskListService.addAll(taskList);

    while (true) {

      // Wait until there are pending task to process
      Workflow.await(this.taskListService::hasUnprocessedTasks);

      final UserTask unprocessedUserTask = this.taskListService.getNextUnprocessedTask();
      logger.info("Processing task {}", unprocessedUserTask);

      scheduleTimerIfDeadline(unprocessedUserTask);

      notifyAssignedToHasChanged(unprocessedUserTask);

      if (unprocessedUserTask.isClosed()) {
        notifyClient(unprocessedUserTask);
      }
    }

    // TODO
    // Workflow.continueAsNew();

  }

  @Override
  public void addTask(UserTask userTask) {
    this.taskListService.add(userTask);
  }

  @Override
  public void validateCompleteTask(CompleteTaskRequest changeTaskRequest) {

    final String taskId = changeTaskRequest.taskId();
    final UserTaskState completed = UserTaskState.Completed;
    if (!taskListService.getTask(taskId).canTaskTransitionToState(completed)) {
      final UserTaskState userTaskState = taskListService.getTask(taskId).getUserTaskState();
      throw new RuntimeException(
          "Task with id ["
              + taskId
              + "], "
              + "with state ["
              + userTaskState
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
  public List<UserTask> getPendingTasks(final UserTaskFilter userTaskFilter) {
    return taskListService.getTasks(userTaskFilter);
  }

  private void notifyClient(final UserTask userTask) {
    try {

      logger.debug("task: " + userTask);
      final String taskId = userTask.getId();
      final String externalWorkflowId = new UserTaskToken(taskId).getWorkflowId();

      logger.debug("externalWorkflowId: " + externalWorkflowId);

      final UserTaskClient taskClient =
          Workflow.newExternalWorkflowStub(UserTaskClient.class, externalWorkflowId);

      if (userTask.getUserTaskState().equals(UserTaskState.Completed)) {

        // cancel timer
        if (timers.containsKey(taskId)) {
          timers.get(taskId).cancel();
          timers.remove(taskId);
        }

        taskClient.completeTaskByToken(taskId, userTask.getResult());
      }
      if (userTask.getUserTaskState().equals(UserTaskState.TimeOut)) {
        taskClient.timeOutTaskByToken(taskId);
      }

    } catch (ApplicationFailure e) {
      throw e;
    }
  }

  private void notifyAssignedToHasChanged(final UserTask userTask) {
    UserTask previousUserTask = userTask.getPreviousState();
    logger.info("Processing previousTask {}", previousUserTask);
    // notify the user...
    if (previousUserTask != null
        && !Objects.equals(userTask.getAssignedTo(), previousUserTask.getAssignedTo())) {
      // Notify use task.getAssignedTo()
    }
  }

  private void scheduleTimerIfDeadline(final UserTask userTask) {
    if (userTask.getDeadline() != null && !timers.containsKey(userTask.getId())) {

      CancellationScope cancelableScope =
          Workflow.newCancellationScope(
              () -> {
                Workflow.newTimer(userTask.getDeadline())
                    .thenApply(
                        t -> {
                          taskListService.timeoutTask(userTask);
                          return null;
                        });
              });

      cancelableScope.run();
      timers.put(userTask.getId(), cancelableScope);
    }
  }
}
