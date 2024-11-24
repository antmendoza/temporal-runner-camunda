package com.antmendoza.temporal.humantask.basicimplementation;


import com.antmendoza.temporal.humantask.TaskInput;
import com.antmendoza.temporal.humantask.WorkflowInput;
import com.antmendoza.temporal.humantask.basicimplementation.workflow.HumanTaskWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class HumanTaskWorkflowClient {

    static final String TASK_QUEUE = "HumanTask_TaskQueue";

    static final String WORKFLOW_ID = "HumanTask_Workflow";


    public static void main(String[] args) {

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        WorkflowClient client = WorkflowClient.newInstance(service);


        // Create the workflow client stub. It is used to start our workflow execution.
        HumanTaskWorkflow workflow =
                client.newWorkflowStub(
                        HumanTaskWorkflow.class,
                        WorkflowOptions.newBuilder()
                                .setWorkflowId(WORKFLOW_ID)
                                .setTaskQueue(TASK_QUEUE)
                                .build());


        WorkflowClient.start(workflow::execute, new WorkflowInput("World", "userIdX"));


        boolean taskCreated = false;

        // Wait until the task is created
        // The workflow is ready to receive the task input
        while (!taskCreated) {

            // in real world application we query an exteran system to list tasks
            taskCreated = workflow.taskCreated();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        final boolean approved = true;
        final HumanTaskWorkflow humanTaskWorkflow = client.newWorkflowStub(HumanTaskWorkflow.class, WORKFLOW_ID);
        humanTaskWorkflow.taskInput(new TaskInput(approved));


        final String result = client.newUntypedWorkflowStub(WORKFLOW_ID).getResult(String.class);

        System.out.println(result);
        System.exit(0);
    }
}
