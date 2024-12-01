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

package com.antmendoza.temporal.usertask.advancedimplementation.workflow;

import com.antmendoza.temporal.usertask.advancedimplementation.taskstore.Task;
import com.antmendoza.temporal.usertask.advancedimplementation.taskstore.TasksList;
import com.antmendoza.temporal.usertask.advancedimplementation.taskstore.WorkflowTaskManager;
import io.temporal.activity.Activity;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;

public class UserTaskImpl implements UserTask {

  private final WorkflowClient workflowClient;

  public UserTaskImpl(WorkflowClient workflowClient) {
    this.workflowClient = workflowClient;
  }

  // This activity is responsible for registering the task to the external service
  @Override
  public void createTask(Task task) {

    final String taskQueue = Activity.getExecutionContext().getInfo().getActivityTaskQueue();

    // We use another workflow to manage the tasks life-cycle
    final WorkflowOptions workflowOptions =
        WorkflowOptions.newBuilder()
            .setWorkflowId(WorkflowTaskManager.WORKFLOW_ID)
            .setTaskQueue(taskQueue)
            .build();

    final Object[] workflowInput = {new TasksList()};
    final Object[] signalInput = {task};
    final String signalName = "addTask";

    // Signal the workflow and start it if the workflow is not running
    workflowClient
        .newUntypedWorkflowStub(WorkflowTaskManager.class.getSimpleName(), workflowOptions)
        .signalWithStart(signalName, signalInput, workflowInput);
  }
}
