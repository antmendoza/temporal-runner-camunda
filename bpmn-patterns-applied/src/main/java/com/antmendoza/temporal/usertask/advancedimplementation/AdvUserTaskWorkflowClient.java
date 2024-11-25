package com.antmendoza.temporal.usertask.advancedimplementation;


import com.antmendoza.temporal.usertask.advancedimplementation.taskstore.Task;
import com.antmendoza.temporal.usertask.advancedimplementation.taskstore.WorkflowTaskManager;
import com.antmendoza.temporal.usertask.advancedimplementation.workflow.*;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.util.ArrayList;
import java.util.List;

public class AdvUserTaskWorkflowClient {

    static final String TASK_QUEUE = "AdvUserTaskWorkflowClient_TaskQueue";

    static final String WORKFLOW_ID = "AdvUserTaskWorkflowClient_Workflow";


    public static void main(String[] args) {

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        WorkflowClient client = WorkflowClient.newInstance(service);


        startWorkflowWithUserTasks(client);


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


            taskCreated = !getAllTasks(client).isEmpty();
        }


        Task tasks = getAllTasks(client).get(0);


        //Completing tasks


        client.newWorkflowStub(WorkflowTaskManager.class, WorkflowTaskManager.WORKFLOW_ID).changeTaskStateTo(new ChangeTaskRequest(
                tasks.getId(), "user1", TaskState.Completed, "approved"
        ));

        final String result = client.newUntypedWorkflowStub(WORKFLOW_ID).getResult(String.class);

        System.out.println(result);
        System.exit(0);
    }

    private static List<Task> getAllTasks(final WorkflowClient client) {

        try{

        final List<Task> allTasks = client.newWorkflowStub(WorkflowTaskManager.class, WorkflowTaskManager.WORKFLOW_ID)
                .getAllTasks();
        return allTasks;
        }catch (Exception e) {
            System.out.println("Start the worker ... " + e);
            //workflow not started, start the worker
            return new ArrayList<>();
        }

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
