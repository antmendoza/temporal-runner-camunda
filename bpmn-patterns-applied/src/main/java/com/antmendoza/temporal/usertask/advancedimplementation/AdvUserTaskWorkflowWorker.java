package com.antmendoza.temporal.usertask.advancedimplementation;

import static com.antmendoza.temporal.usertask.advancedimplementation.AdvUserTaskWorkflowClient.TASK_QUEUE;

import com.antmendoza.temporal.usertask.activities.ActivitiesImpl;
import com.antmendoza.temporal.usertask.advancedimplementation.taskstore.WorkflowTaskManagerImpl;
import com.antmendoza.temporal.usertask.advancedimplementation.workflow.UserTaskImpl;
import com.antmendoza.temporal.usertask.advancedimplementation.workflow.WorkflowWithTasksImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class AdvUserTaskWorkflowWorker {

  public static void main(String[] args) {

    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    WorkflowClient client = WorkflowClient.newInstance(service);

    WorkerFactory factory = WorkerFactory.newInstance(client);

    Worker worker = factory.newWorker(TASK_QUEUE);

    /*
     * Register our workflow implementation with the worker.
     * Workflow implementations must be known to the worker at runtime in
     * order to dispatch workflow tasks.
     */
    worker.registerWorkflowImplementationTypes(WorkflowWithTasksImpl.class);
    worker.registerWorkflowImplementationTypes(WorkflowTaskManagerImpl.class);

    /*
     * Register our Activity Types with the Worker. Since Activities are stateless and thread-safe,
     * the Activity Type is a shared instance.
     */
    worker.registerActivitiesImplementations(new ActivitiesImpl());
    worker.registerActivitiesImplementations(new UserTaskImpl(client));

    factory.start();
  }
}
