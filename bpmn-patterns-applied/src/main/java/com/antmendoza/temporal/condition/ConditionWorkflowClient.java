package com.antmendoza.temporal.condition;


import com.antmendoza.temporal.condition.workflow.ConditionWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class ConditionWorkflowClient {

  static final String TASK_QUEUE = "ConditionWorkflowClient_taskqueue";

  static final String WORKFLOW_ID = "ConditionWorkflow_workflowid";


  public static void main(String[] args) {

    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    WorkflowClient client = WorkflowClient.newInstance(service);


    // Create the workflow client stub. It is used to start our workflow execution.
    ConditionWorkflow workflow =
        client.newWorkflowStub(
                ConditionWorkflow.class,
            WorkflowOptions.newBuilder()
                .setWorkflowId(WORKFLOW_ID)
                .setTaskQueue(TASK_QUEUE)
                .build());

    String greeting = workflow.execute("World");

    System.out.println(greeting);
    System.exit(0);
  }
}
