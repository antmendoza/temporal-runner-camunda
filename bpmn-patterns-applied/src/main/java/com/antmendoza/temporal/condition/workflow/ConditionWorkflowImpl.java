package com.antmendoza.temporal.condition.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class ConditionWorkflowImpl implements ConditionWorkflow {

  private final Activities activities =
      Workflow.newActivityStub(
          Activities.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(2)).build());

  @Override
  public String execute(String workflowInput) {
    int activity1_Output = activities.calculateX(workflowInput);

    if (activity1_Output > 5) {
      return activities.x_greater_than_5(workflowInput);
    }
    return activities.x_less_than_or_equal_to_5(workflowInput);
  }
}
