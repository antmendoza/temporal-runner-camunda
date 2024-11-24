package com.antmendoza.temporal.humantask;


import com.antmendoza.temporal.humantask.workflow.ActivitiesImpl;
import com.antmendoza.temporal.humantask.workflow.HumanTaskWorkflowImpl;
import com.antmendoza.temporal.humantask.workflow.UserTasksImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import static com.antmendoza.temporal.humantask.HumanTaskWorkflowClient.TASK_QUEUE;

public class HumanTaskWorkflowWorker {


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
        worker.registerWorkflowImplementationTypes(HumanTaskWorkflowImpl.class);

        /*
         * Register our Activity Types with the Worker. Since Activities are stateless and thread-safe,
         * the Activity Type is a shared instance.
         */
        worker.registerActivitiesImplementations(new ActivitiesImpl());
        worker.registerActivitiesImplementations(new UserTasksImpl());


        factory.start();


    }
}