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

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.CompletablePromise;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * This class responsibility is to register the task in the external system and waits for the
 * external system to signal back.
 */
public class TaskService<R> {

  private final UserTask userTask =
      Workflow.newActivityStub(
              UserTask.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(5)).build());

  private TaskManager tasksManager = new TaskManager();

  private final Logger logger = Workflow.getLogger(TaskService.class);

  public TaskService() {

    // This listener exposes a signal method that clients use to notify the task has been completed
    Workflow.registerListener(
        new TaskClient() {
          @Override
          public void completeTaskByToken(final String taskToken) {
            logger.info("Completing task with token: " + taskToken);
            tasksManager.completeTask(taskToken);
          }
        });
  }

  public Task userTask(Task task) {

    logger.info("Before creating task : " + task);

    // Activity implementation is responsible for registering the task to the external service
    // (which is responsible for managing the task life-cycle)
    userTask.createTask(task);

    logger.info("Task created: " + task);

    tasksManager.waitForTaskCompletion(task);

    logger.info("Task completed: " + task);
    return task;
  }

  private class TaskManager {

    private final Map<String, CompletablePromise<R>> tasks = new HashMap<>();

    public void waitForTaskCompletion(final Task task) {
      final CompletablePromise<R> promise = Workflow.newPromise();
      tasks.put(task.getId(), promise);
      // Wait promise to complete
      promise.get();
    }

    public void completeTask(final String taskToken) {

      final CompletablePromise<R> completablePromise = tasks.get(taskToken);
      completablePromise.complete(null);
    }
  }
}
