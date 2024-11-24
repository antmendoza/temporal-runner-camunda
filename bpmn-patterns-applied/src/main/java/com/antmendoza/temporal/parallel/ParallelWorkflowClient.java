package com.antmendoza.temporal.parallel;


import com.antmendoza.temporal.parallel.workflow.ParallelWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class ParallelWorkflowClient {

  static final String TASK_QUEUE = "ParallelWorkflow_taskqueue";

  static final String WORKFLOW_ID = "ParallelWorkflow_workflowid";


  public static void main(String[] args) {

    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    WorkflowClient client = WorkflowClient.newInstance(service);


    // Create the workflow client stub. It is used to start our workflow execution.
    ParallelWorkflow workflow =
        client.newWorkflowStub(
                ParallelWorkflow.class,
            WorkflowOptions.newBuilder()
                .setWorkflowId(WORKFLOW_ID)
                .setTaskQueue(TASK_QUEUE)
                .build());

    String greeting = workflow.execute("World");

    System.out.println(greeting);
    System.exit(0);
  }
}
