package com.antmendoza.temporal.parallel.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ParallelWorkflowImpl implements ParallelWorkflow {

  private final Activities activities =
      Workflow.newActivityStub(
          Activities.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(2)).build());

  @Override
  public String execute(String workflowInput) {
    String activity1_Output = activities.activity1(workflowInput);

    final List<Promise<String>> parallelExecution = new ArrayList<>();
    parallelExecution.add(Async.function(activities::activity2, activity1_Output));
    parallelExecution.add(Async.function(activities::activity3, activity1_Output));

    // Wait for all to complete, note that this will throw an error if one of them fail
    Promise.allOf(parallelExecution).get();

    final List<String> results = new ArrayList();

    // Loop through promises and get results
    for (Promise<String> promise : parallelExecution) {
      if (promise.getFailure() == null) {
        results.add(promise.get());
      }
    }

    return results.toString();
  }
}
