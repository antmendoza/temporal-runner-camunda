/*
 *  Copyright (c) 2020 Temporal Technologies, Inc. All Rights Reserved
 *
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package com.antmendoza;

import com.antmendoza.camunda.CamundaReader;
import com.antmendoza.camunda.JsonMapper;
import com.antmendoza.jsonmodel.JsonModel;
import com.antmendoza.workflow.ActivitiesImpl;
import com.antmendoza.workflow.MyWorkflow;
import com.antmendoza.workflow.MyWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

/**
 * Sample Temporal Workflow Definition that executes a single Activity.
 */
public class WorkflowStarter {

    // Define the task queue name
    static final String TASK_QUEUE = "HelloActivityTaskQueue";

    // Define our workflow unique id
    static final String WORKFLOW_ID = "HelloActivityWorkflow";

    /**
     * With our Workflow and Activities defined, we can now start execution. The main method starts
     * the worker and then the workflow.
     */
    public static void main(String[] args) {

        BpmnModelInstance xml = new CamundaReader().readXML("simpleActivity.bpmn");


        JsonModel.Root jsonModel = new JsonMapper().toJsonModel(xml);


        // Get a Workflow service stub.
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        WorkflowClient client = WorkflowClient.newInstance(service);

        WorkerFactory factory = WorkerFactory.newInstance(client);

        Worker worker = factory.newWorker(TASK_QUEUE);

        worker.registerWorkflowImplementationTypes(MyWorkflowImpl.class);

        worker.registerActivitiesImplementations(new ActivitiesImpl());


        factory.start();

        // Create the workflow client stub. It is used to start our workflow execution.
        MyWorkflow workflow =
                client.newWorkflowStub(
                        MyWorkflow.class,
                        WorkflowOptions.newBuilder()
                                .setWorkflowId(WORKFLOW_ID)
                                .setTaskQueue(TASK_QUEUE)
                                .build());

        String greeting = workflow.run(jsonModel, null);

        // Display workflow execution results
        System.out.println(greeting);
        // System.exit(0);
    }




}
