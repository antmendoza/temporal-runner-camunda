package com.antmendoza.temporal.sequential.workflow;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.testing.TestWorkflowRule;
import java.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class WorkflowTest {

  @Rule
  public TestWorkflowRule testWorkflowRule =
      TestWorkflowRule.newBuilder().setDoNotStart(true).build();

  @Test
  public void simpleActivity() {

    testWorkflowRule.getWorker().registerWorkflowImplementationTypes(SequentialWorkflowImpl.class);

    testWorkflowRule.getWorker().registerActivitiesImplementations(new MyMockedActivities());

    testWorkflowRule.getTestEnvironment().start();

    final String taskQueue = testWorkflowRule.getTaskQueue();

    final WorkflowClient workflowClient = testWorkflowRule.getWorkflowClient();
    final String workflowId = "my-business-id";
    final SequentialWorkflow workflow =
        workflowClient.newWorkflowStub(
            SequentialWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue(taskQueue)
                .setWorkflowId(workflowId)
                .setWorkflowRunTimeout(Duration.ofSeconds(10))
                .build());

    WorkflowClient.start(workflow::execute, "my-input");

    final WorkflowStub workflowStub = workflowClient.newUntypedWorkflowStub(workflowId);

    String result = workflowStub.getResult(String.class);

    Assert.assertNotNull(result);
    Assert.assertEquals(result, "activity1-output; activity2-output; activity3-output");
  }

  private static class MyMockedActivities implements Activities {
    @Override
    public String activity1(final String input) {
      return "activity1-output";
    }

    @Override
    public String activity2(final String input) {
      return input + "; activity2-output";
    }

    @Override
    public String activity3(final String input) {
      return input + "; activity3-output";
    }
  }
}
