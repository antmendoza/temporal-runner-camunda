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

import com.antmendoza.temporal.usertask.activities.Activities;
import com.antmendoza.temporal.usertask.advancedimplementation.taskstore.Task;
import com.antmendoza.temporal.usertask.advancedimplementation.taskstore.TaskToken;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import org.slf4j.Logger;

/** Workflow that creates three task and waits for them to complete */
public class WorkflowWithTasksImpl implements WorkflowWithTasks {

  final GenerateTaskToken taskToken = new GenerateTaskToken();
  private final Logger logger = Workflow.getLogger(WorkflowWithTasksImpl.class);
  private final TaskService taskService = new TaskService();
  private final Activities activities =
      Workflow.newActivityStub(
          Activities.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(2)).build());

  @Override
  public String execute() {

    activities.activity1("some input");

    final Task task = new Task(taskToken.getNext(), "user1", "TODO 1");

    // Block until the tasks is completed
    final String taskResult = taskService.userTask(task);
    boolean isValid = taskResult.equals("approved");

    if (isValid) {
      activities.activity2("other input 2");
    } else {
      activities.activity3("other input 3");
    }

    return "done";
  }

  private static class GenerateTaskToken {
    private int taskToken = 1;

    public String getNext() {
      final String workflowId = Workflow.getInfo().getWorkflowId();
      return new TaskToken(workflowId, "" + taskToken++).getToken();
    }
  }
}
