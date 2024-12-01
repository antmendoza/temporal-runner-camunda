package com.antmendoza.temporal.iteration.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class IterationWorkflowImpl implements IterationWorkflow {

  private final Activities activities =
      Workflow.newActivityStub(
          Activities.class,
          ActivityOptions.newBuilder()
              // For long-running activities consider increasing the start_to_close
              // and heartbeat
              .setStartToCloseTimeout(Duration.ofSeconds(2))
              .build());

  @Override
  public String execute(String workflowInput) {

    final String activity1_Output = activities.prepareOrder(workflowInput);

    int numIterations = 0;

    while (true) {

      final String activity2_Output = activities.preparePizza(activity1_Output);

      final String activity3_Output = activities.bakePizza(activity2_Output, ++numIterations);

      if (activity3_Output.equals("qualityOK")) {
        break;
      }

      if (Workflow.getInfo().isContinueAsNewSuggested()) {
        // TODO https://docs.temporal.io/workflows#continue-as-new
      }
    }

    activities.deliverPizza(workflowInput);

    return "done";
  }
}
