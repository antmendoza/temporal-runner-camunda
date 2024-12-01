package com.antmendoza.temporal.sequential.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class SequentialWorkflowImpl implements SequentialWorkflow {

  private final Activities activities =
      Workflow.newActivityStub(
          Activities.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(2)).build());

  @Override
  public String execute(String workflowInput) {
    String activity1_Output = activities.activity1(workflowInput);
    String activity2_Output = activities.activity2(activity1_Output);
    String activity3_Output = activities.activity3(activity2_Output);

    return activity3_Output;
  }
}
