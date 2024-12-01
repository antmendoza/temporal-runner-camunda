package com.antmendoza.temporal.usertask.advancedimplementation.workflow;

import com.antmendoza.temporal.usertask.advancedimplementation.usertasks.UserTask;
import com.antmendoza.temporal.usertask.advancedimplementation.usertasks.UserTaskClient;
import com.antmendoza.temporal.usertask.advancedimplementation.usertasks.UserTaskTimeoutException;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.CompletablePromise;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

/**
 * This class responsibility is to register the task in the external system and waits for the
 * external system to signal back.
 */
public class UserTaskService {

  // Can be local activity
  private final ActivityUserTask userTask =
      Workflow.newActivityStub(
          ActivityUserTask.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(5)).build());

  private TaskManager tasksManager = new TaskManager();

  private final Logger logger = Workflow.getLogger(UserTaskService.class);

  public UserTaskService() {

    // This listener exposes a signal method that clients use to notify the task has been completed
    Workflow.registerListener(
        new UserTaskClient() {
          @Override
          public void completeTaskByToken(final String taskToken, final String result) {
            logger.info("Completing task with token: " + taskToken);
            tasksManager.completeTask(taskToken, result);
          }

          @Override
          public void timeOutTaskByToken(final String taskToken) {
            logger.info("timed out task with token: " + taskToken);
            tasksManager.timeoutTask(taskToken);
          }
        });
  }

  /**
   * Create a user task , managed by {@link com.antmendoza.temporal.usertask.advancedimplementation.usertasks.WorkflowTaskManager},
   * and blocks until the task is completed
   *
   * This method will throw an exception {@link UserTaskTimeoutException} if the task has a deadline and it times out.
   *
   * @param userTask
   * @return
   */
  public String createUserTask(UserTask userTask) {

    logger.info("Before creating task : {}", userTask);

    // Activity implementation is responsible for registering the task to the external service
    // (which is responsible for managing the task life-cycle)
    this.userTask.createTask(userTask);

    logger.info("Task created: {}", userTask);

    String result = tasksManager.waitForTaskCompletion(userTask).getResult();

    logger.info("Task completed: {} result: {}", userTask, result);

    return result;
  }

  private class TaskManager {

    private final Map<String, CompletablePromise<String>> tasks = new HashMap<>();

    public UserTask waitForTaskCompletion(final UserTask userTask) {
      final CompletablePromise<String> promise = Workflow.newPromise();
      tasks.put(userTask.getId(), promise);
      // Wait promise to complete
      String result = promise.get();

      return userTask.withResult(result);
    }

    public void completeTask(final String taskToken, String result) {

      final CompletablePromise<String> completablePromise = tasks.get(taskToken);
      completablePromise.complete(result);
    }

    public void timeoutTask(final String taskToken) {
      final CompletablePromise<String> completablePromise = tasks.get(taskToken);
      completablePromise.completeExceptionally(
          new UserTaskTimeoutException("taskToken [" + taskToken + "]"));
    }
  }
}
