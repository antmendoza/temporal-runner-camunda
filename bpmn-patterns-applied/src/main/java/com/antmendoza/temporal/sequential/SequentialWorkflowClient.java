package com.antmendoza.temporal.sequential;

import com.antmendoza.temporal.sequential.workflow.SequentialWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class SequentialWorkflowClient {

  static final String TASK_QUEUE = "HelloActivityTaskQueue";

  static final String WORKFLOW_ID = "HelloActivityWorkflow";

  public static void main(String[] args) {

    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    WorkflowClient client = WorkflowClient.newInstance(service);

    // Create the workflow client stub. It is used to start our workflow execution.
    SequentialWorkflow workflow =
        client.newWorkflowStub(
            SequentialWorkflow.class,
            WorkflowOptions.newBuilder()
                .setWorkflowId(WORKFLOW_ID)
                .setTaskQueue(TASK_QUEUE)
                .build());

    String greeting = workflow.execute("World");

    System.out.println(greeting);
    System.exit(0);
  }
}
