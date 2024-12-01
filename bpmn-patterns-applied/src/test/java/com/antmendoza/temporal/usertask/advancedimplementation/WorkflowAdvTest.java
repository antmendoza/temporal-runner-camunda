package com.antmendoza.temporal.usertask.advancedimplementation;

import com.antmendoza.temporal.usertask.activities.ActivitiesImpl;
import com.antmendoza.temporal.usertask.advancedimplementation.taskstore.Task;
import com.antmendoza.temporal.usertask.advancedimplementation.taskstore.WorkflowTaskManager;
import com.antmendoza.temporal.usertask.advancedimplementation.taskstore.WorkflowTaskManagerImpl;
import com.antmendoza.temporal.usertask.advancedimplementation.workflow.*;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.worker.Worker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class WorkflowAdvTest {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowAdvTest.class);
    private static final boolean externalService = false;
    @Rule
    public TestWorkflowRule testWorkflowRule =
            getBuilder()
                    .build();

    @After
    public void tearDown() {
        testWorkflowRule.getTestEnvironment().shutdown();
    }

    @Test
    public void simpleActivity() {

        final Worker worker = testWorkflowRule.getWorker();
        worker.registerWorkflowImplementationTypes(WorkflowTaskManagerImpl.class);
        worker.registerWorkflowImplementationTypes(WorkflowWithTasksImpl.class);

        worker.registerActivitiesImplementations(new ActivitiesImpl());
        final WorkflowClient workflowClient = testWorkflowRule.getWorkflowClient();
        worker.registerActivitiesImplementations(new UserTaskImpl(workflowClient));

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
                                //Only for test purpose
                                .setWorkflowRunTimeout(Duration.ofSeconds(10))
                                .build());

        WorkflowClient.start(workflow::execute);

        final WorkflowStub workflowStub = workflowClient.newUntypedWorkflowStub(workflowId);


        final WorkflowTaskManager workflowTaskManager = getWorkflowTaskManager(workflowClient);
        final List<Task> tasks = getAllTasks(workflowTaskManager);

        logger.info("tasks {}", tasks);

        tasks.forEach(task -> {
            final ChangeTaskRequest newState = new ChangeTaskRequest(task.getId(), "-", TaskState.Completed, "approved");
            workflowTaskManager.changeTaskStateTo(newState);
        });


        String result = workflowStub.getResult(String.class);

        Assert.assertNotNull(result);
        Assert.assertEquals("done", result);
    }



    private static TestWorkflowRule.Builder getBuilder() {
        final TestWorkflowRule.Builder builder = TestWorkflowRule.newBuilder()
                .setDoNotStart(true);


        if (externalService) {
            builder.setUseExternalService(true)
                    .setNamespace("default");

        }
        return builder;

    }

    private static List<Task> getAllTasks(final WorkflowTaskManager workflowTaskManager) {

        for (int i = 0; i < 5; i++) {
            try {

                final List<Task> allTasks = workflowTaskManager.getAllTasks();

                logger.info("allTasks {}", allTasks);

                if (!allTasks.isEmpty()) {
                    return allTasks;
                }
            } catch (Exception ignored) {
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    private static WorkflowTaskManager getWorkflowTaskManager(final WorkflowClient workflowClient) {
        return workflowClient.newWorkflowStub(WorkflowTaskManager.class, WorkflowTaskManager.WORKFLOW_ID);
    }


}
