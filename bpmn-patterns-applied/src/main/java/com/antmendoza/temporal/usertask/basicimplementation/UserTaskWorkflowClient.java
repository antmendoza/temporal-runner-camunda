package com.antmendoza.temporal.usertask.basicimplementation;

import com.antmendoza.temporal.usertask.TaskInput;
import com.antmendoza.temporal.usertask.WorkflowInput;
import com.antmendoza.temporal.usertask.basicimplementation.workflow.UserTaskWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class UserTaskWorkflowClient {

  static final String TASK_QUEUE = "UserTask_TaskQueue";

  static final String WORKFLOW_ID = "UserTask_Workflow";

  public static void main(String[] args) {

    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    WorkflowClient client = WorkflowClient.newInstance(service);

    // Create the workflow client stub. It is used to start our workflow execution.
    UserTaskWorkflow workflow =
        client.newWorkflowStub(
            UserTaskWorkflow.class,
            WorkflowOptions.newBuilder()
                .setWorkflowId(WORKFLOW_ID)
                .setTaskQueue(TASK_QUEUE)
                .build());

    WorkflowClient.start(workflow::execute, new WorkflowInput("World", "userIdX"));

    boolean taskCreated = false;

    // Wait until the task is created
    // The workflow is ready to receive the task input
    while (!taskCreated) {

      // in real world application we query an external system to list tasks
      taskCreated = workflow.taskCreated();
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    final boolean approved = true;
    final UserTaskWorkflow userTaskWorkflow =
        client.newWorkflowStub(UserTaskWorkflow.class, WORKFLOW_ID);
    userTaskWorkflow.taskInput(new TaskInput(approved));

    final String result = client.newUntypedWorkflowStub(WORKFLOW_ID).getResult(String.class);

    System.out.println(result);
    System.exit(0);
  }
}
