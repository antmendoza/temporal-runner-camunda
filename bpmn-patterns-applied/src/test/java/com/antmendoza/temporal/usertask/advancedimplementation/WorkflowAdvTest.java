package com.antmendoza.temporal.usertask.advancedimplementation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.antmendoza.temporal.usertask.activities.Activities;
import com.antmendoza.temporal.usertask.activities.ActivitiesImpl;
import com.antmendoza.temporal.usertask.advancedimplementation.tasks.*;
import com.antmendoza.temporal.usertask.advancedimplementation.workflow.*;
import com.antmendoza.temporal.usertask.advancedimplementation.workflow.onetask.WorkflowWithTasksImpl;
import com.antmendoza.temporal.usertask.advancedimplementation.workflow.onetaskwithdeadline.WorkflowWithTaskWithDeadlineImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.worker.Worker;
import java.time.Duration;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowAdvTest {

  private static final Logger logger = LoggerFactory.getLogger(WorkflowAdvTest.class);
  private static final boolean externalService = false;
  @Rule public TestWorkflowRule testWorkflowRule = getBuilder().build();

  @After
  public void tearDown() {
    testWorkflowRule.getTestEnvironment().shutdown();
  }

  @Test
  public void testOneTasks() {

    final Worker worker = testWorkflowRule.getWorker();
    worker.registerWorkflowImplementationTypes(WorkflowTaskManagerImpl.class);
    worker.registerWorkflowImplementationTypes(WorkflowWithTasksImpl.class);

    worker.registerActivitiesImplementations(new ActivitiesImpl());

    final WorkflowClient workflowClient = testWorkflowRule.getWorkflowClient();
    worker.registerActivitiesImplementations(new ActivityUserTaskImpl(workflowClient));

    final TestWorkflowEnvironment testEnvironment = testWorkflowRule.getTestEnvironment();
    testEnvironment.start();

    final String taskQueue = testWorkflowRule.getTaskQueue();

    final String workflowId = "myBusinessId";
    final WorkflowWithTasks workflow =
        workflowClient.newWorkflowStub(
            WorkflowWithTasks.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue(taskQueue)
                .setWorkflowId(workflowId)
                // Only for test purpose
                .setWorkflowRunTimeout(Duration.ofSeconds(10))
                .build());

    WorkflowClient.start(workflow::execute);

    final WorkflowStub workflowStub = workflowClient.newUntypedWorkflowStub(workflowId);

    final WorkflowTaskManager workflowTaskManager = getWorkflowTaskManager(workflowClient);
    waitUntilTaskManagerHasTasks(workflowTaskManager);

    Assert.assertEquals(1, workflowTaskManager.getPendingTasks(TaskFilter.OPEN_TASKS).size());

    final List<Task> tasks = workflowTaskManager.getPendingTasks(TaskFilter.OPEN_TASKS);

    logger.info("-> tasks {}", tasks);

    tasks.forEach(
        task -> {
          final CompleteTaskRequest newState = new CompleteTaskRequest(task.getId(), "approved");
          logger.info("completing task {}", task.getId());
          workflowTaskManager.completeTask(newState);
        });

    String result = workflowStub.getResult(String.class);

    Assert.assertNotNull(result);
    Assert.assertEquals("done", result);

    Assert.assertEquals(0, workflowTaskManager.getPendingTasks(TaskFilter.OPEN_TASKS).size());
  }

  @Test
  public void testOneTaskWithDeadline() {
    Activities activities = mock(Activities.class, withSettings().withoutAnnotations());

    when(activities.activity1(any())).thenReturn("Hello World!");
    when(activities.activity4(any())).thenReturn("Hello World!");

    final WorkflowClient workflowClient = testWorkflowRule.getWorkflowClient();

    final Worker worker = testWorkflowRule.getWorker();
    worker.registerWorkflowImplementationTypes(WorkflowTaskManagerImpl.class);
    worker.registerWorkflowImplementationTypes(WorkflowWithTaskWithDeadlineImpl.class);

    worker.registerActivitiesImplementations(activities);

    worker.registerActivitiesImplementations(new ActivityUserTaskImpl(workflowClient));

    final TestWorkflowEnvironment testEnvironment = testWorkflowRule.getTestEnvironment();
    testEnvironment.start();

    final String taskQueue = testWorkflowRule.getTaskQueue();

    final String workflowId = "myBusinessId";
    final WorkflowWithTasks workflow =
        workflowClient.newWorkflowStub(
            WorkflowWithTasks.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue(taskQueue)
                .setWorkflowId(workflowId)
                // Only for test purpose
                .setWorkflowRunTimeout(Duration.ofSeconds(10))
                .build());

    WorkflowClient.start(workflow::execute);

    final WorkflowStub workflowStub = workflowClient.newUntypedWorkflowStub(workflowId);

    final WorkflowTaskManager workflowTaskManager = getWorkflowTaskManager(workflowClient);
    waitUntilTaskManagerHasTasks(workflowTaskManager);

    Assert.assertEquals(1, workflowTaskManager.getPendingTasks(TaskFilter.OPEN_TASKS).size());

    // Do not complete tasks and wait for task deadline
    testWorkflowRule.getTestEnvironment().sleep(Duration.ofSeconds(5));

    String result = workflowStub.getResult(String.class);

    Assert.assertNotNull(result);
    Assert.assertEquals("task with deadline", result);

    verify(activities, times(1)).activity4("other input 4");

    // Task will be removed automatically from open tasks
    Assert.assertEquals(0, workflowTaskManager.getPendingTasks(TaskFilter.OPEN_TASKS).size());
  }

  private static TestWorkflowRule.Builder getBuilder() {
    final TestWorkflowRule.Builder builder = TestWorkflowRule.newBuilder().setDoNotStart(true);

    if (externalService) {
      builder.setUseExternalService(true).setNamespace("default");
    }
    return builder;
  }

  private static void waitUntilTaskManagerHasTasks(final WorkflowTaskManager workflowTaskManager) {

    for (int i = 0; i < 5; i++) {
      try {

        final List<Task> allTasks = workflowTaskManager.getPendingTasks(TaskFilter.OPEN_TASKS);

        logger.info("allTasks {}", allTasks);

        if (!allTasks.isEmpty()) {
          return;
        }
      } catch (Exception ignored) {
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static WorkflowTaskManager getWorkflowTaskManager(final WorkflowClient workflowClient) {
    return workflowClient.newWorkflowStub(
        WorkflowTaskManager.class, WorkflowTaskManager.WORKFLOW_ID);
  }
}
