package com.antmendoza.temporal.usertask.advancedimplementation.workflow.onetask;

import com.antmendoza.temporal.usertask.activities.Activities;
import com.antmendoza.temporal.usertask.advancedimplementation.usertasks.UserTask;
import com.antmendoza.temporal.usertask.advancedimplementation.workflow.UserTaskService;
import com.antmendoza.temporal.usertask.advancedimplementation.workflow.WorkflowWithTasks;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import org.slf4j.Logger;

/** Workflow that creates three task and waits for them to complete */
public class WorkflowWithTasksImpl implements WorkflowWithTasks {

  final GenerateTaskToken taskToken = new GenerateTaskToken();
  private final Logger logger = Workflow.getLogger(WorkflowWithTasksImpl.class);
  private final UserTaskService taskService = new UserTaskService();
  private final Activities activities =
      Workflow.newActivityStub(
          Activities.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(2)).build());

  @Override
  public String execute() {

    activities.activity1("some input");

    final UserTask userTask =
        new UserTask.Builder().id(taskToken.getNext()).assignedTo("user1").title("TODO 1").build();

    // Block until the tasks is completed
    final String taskResult = taskService.createUserTask(userTask);
    boolean isValid = taskResult.equals("approved");

    if (isValid) {
      activities.activity2("other input 2");
    } else {
      activities.activity3("other input 3");
    }

    return "done";
  }
}
