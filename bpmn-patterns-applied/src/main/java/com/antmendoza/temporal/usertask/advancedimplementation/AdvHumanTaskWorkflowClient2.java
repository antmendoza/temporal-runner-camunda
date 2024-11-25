package com.antmendoza.temporal.usertask.advancedimplementation;


import com.antmendoza.temporal.usertask.advancedimplementation.workflow.*;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.util.List;

public class AdvHumanTaskWorkflowClient2 {

    static final String TASK_QUEUE = "AdvHumanTaskWorkflowClient_TaskQueue";

    static final String WORKFLOW_ID = "AdvHumanTaskWorkflowClient_Workflow";


    public static void main(String[] args) {

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        WorkflowClient client = WorkflowClient.newInstance(service);



        boolean taskCreated = false;

        // Wait until the task is created
        // The workflow is ready to receive the task input
        while (!taskCreated) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // wait until one task in WorkflowTaskManager is created
            final List<Task> allTasks = getWorkflowTaskManager(client)
                    .getAllTasks();


            System.out.println("All tasks: " + allTasks);

            taskCreated = !allTasks.isEmpty();


        }



        System.exit(0);
    }

    private static WorkflowTaskManager getWorkflowTaskManager(final WorkflowClient client) {
        // Create the workflow client stub. It is used to start our workflow execution.
        WorkflowTaskManager workflowTaskManager =
                client.newWorkflowStub(
                        WorkflowTaskManager.class,
                        WorkflowOptions.newBuilder()
                                .setWorkflowId(WorkflowTaskManager.WORKFLOW_ID)
                                .setTaskQueue(TASK_QUEUE)
                                .build());


        return client.newWorkflowStub(WorkflowTaskManager.class, WorkflowTaskManager.WORKFLOW_ID);


    }

    private static void startWorkflowWithUserTasks(final WorkflowClient client) {
        // Create the workflow client stub. It is used to start our workflow execution.
        WorkflowWithTasks workflow =
                client.newWorkflowStub(
                        WorkflowWithTasks.class,
                        WorkflowOptions.newBuilder()
                                .setWorkflowId(WORKFLOW_ID)
                                .setTaskQueue(TASK_QUEUE)
                                .build());


        WorkflowClient.start(workflow::execute);
    }
}
