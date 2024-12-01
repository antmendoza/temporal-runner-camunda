package com.antmendoza.temporal.usertask.basicimplementation.workflow;

import com.antmendoza.temporal.usertask.TaskInput;
import com.antmendoza.temporal.usertask.WorkflowInput;
import com.antmendoza.temporal.usertask.activities.Activities;
import io.temporal.activity.ActivityOptions;
import io.temporal.activity.LocalActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class UserTaskWorkflowImpl implements UserTaskWorkflow {

  private final Activities activities =
      Workflow.newActivityStub(
          Activities.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(2)).build());

  private final UserTasks userTasks =
      Workflow.newLocalActivityStub(
          UserTasks.class,
          LocalActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(1)).build());
  private TaskInput taskInput;
  private boolean taskCreated = false;

  public String execute(final WorkflowInput workflowInput) {
    activities.activity1(workflowInput.someInput());

    boolean isValid = this.userValidation(workflowInput.userId());

    if (isValid) {
      activities.activity2(workflowInput.someInput());
    } else {
      activities.activity3(workflowInput.someInput());
    }

    return "done";
  }

  @Override
  public void taskInput(final TaskInput taskInput) {
    this.taskInput = taskInput;
  }

  @Override
  public boolean taskCreated() {
    return this.taskCreated;
  }

  private boolean userValidation(final String userId) {

    String taskId = userTasks.createTasks(userId);
    this.taskCreated = true;

    Workflow.await(() -> this.taskInput != null);
    return this.taskInput.approved();
  }
}
