package com.antmendoza.temporal.usertask.advancedimplementation.workflow;

import com.antmendoza.temporal.usertask.advancedimplementation.usertasks.UserTask;
import com.antmendoza.temporal.usertask.advancedimplementation.usertasks.UserTasksList;
import com.antmendoza.temporal.usertask.advancedimplementation.usertasks.WorkflowTaskManager;
import io.temporal.activity.Activity;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;

public class ActivityUserTaskImpl implements ActivityUserTask {

  private final WorkflowClient workflowClient;

  public ActivityUserTaskImpl(WorkflowClient workflowClient) {
    this.workflowClient = workflowClient;
  }

  // This activity is responsible for registering the task to the external service
  @Override
  public void createTask(UserTask userTask) {

    final String taskQueue = Activity.getExecutionContext().getInfo().getActivityTaskQueue();

    // We use another workflow to manage the tasks life-cycle
    final WorkflowOptions workflowOptions =
        WorkflowOptions.newBuilder()
            .setWorkflowId(WorkflowTaskManager.WORKFLOW_ID)
            .setTaskQueue(taskQueue)
            .build();

    final Object[] workflowInput = {new UserTasksList()};
    final Object[] signalInput = {userTask};
    final String signalName = "addTask";

    // Signal the workflow and start it if the workflow is not running
    workflowClient
        .newUntypedWorkflowStub(WorkflowTaskManager.class.getSimpleName(), workflowOptions)
        .signalWithStart(signalName, signalInput, workflowInput);
  }
}
