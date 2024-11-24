package com.antmendoza.temporal.iteration;


import com.antmendoza.temporal.iteration.workflow.IterationWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class IterationWorkflowClient {

  static final String TASK_QUEUE = "IterationWorkflowClientTaskQueue";

  static final String WORKFLOW_ID = "IterationWorkflowClientWorkflow";


  public static void main(String[] args) {

    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    WorkflowClient client = WorkflowClient.newInstance(service);


    // Create the workflow client stub. It is used to start our workflow execution.
    IterationWorkflow workflow =
        client.newWorkflowStub(
                IterationWorkflow.class,
            WorkflowOptions.newBuilder()
                .setWorkflowId(WORKFLOW_ID)
                .setTaskQueue(TASK_QUEUE)
                .build());

    String greeting = workflow.execute("World");

    System.out.println(greeting);
    System.exit(0);
  }
}
