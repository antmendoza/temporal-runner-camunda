package com.antmendoza;

import com.antmendoza.camunda.CamundaReader;
import com.antmendoza.camunda.JsonMapper;
import com.antmendoza.jsonmodel.JsonModel;
import com.antmendoza.workflow.ActivitiesImpl;
import com.antmendoza.workflow.MyWorkflow;
import com.antmendoza.workflow.MyWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.testing.TestWorkflowRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Rule;
import org.junit.Test;

import java.time.Duration;
import java.util.List;

public class WorkflowTest {

    @Rule
    public TestWorkflowRule testWorkflowRule =
            TestWorkflowRule.newBuilder().setDoNotStart(true)
                    .setUseExternalService(true)
                    .setNamespace("default").build();

    @Test
    public void simpleActivity() {

        testWorkflowRule
                .getWorker()
                .registerWorkflowImplementationTypes(MyWorkflowImpl.class);

        testWorkflowRule
                .getWorker()
                .registerActivitiesImplementations(new ActivitiesImpl());


        testWorkflowRule.getTestEnvironment().start();



        final String taskQueue = testWorkflowRule.getTaskQueue();



        for (String workflowId : List.of("simpleActivity.bpmn", "simpleHumanTask.bpmn", "simpleHumanTaskWithTimer.bpmn")){
            final WorkflowClient workflowClient = testWorkflowRule
                    .getWorkflowClient();
            final MyWorkflow workflow =
                    workflowClient
                            .newWorkflowStub(
                                    MyWorkflow.class,
                                    WorkflowOptions.newBuilder()
                                            .setTaskQueue(taskQueue)
                                            .setWorkflowId(workflowId)
                                            .setWorkflowRunTimeout(Duration.ofSeconds(10))
                                            .build());

            System.out.println("Executing " + workflowId);

            BpmnModelInstance xml = new CamundaReader().readXML(workflowId);
            JsonModel.Root jsonModel = new JsonMapper().toJsonModel(xml);

            WorkflowClient.start(workflow::run, jsonModel, null);



            final WorkflowStub workflowStub = workflowClient.newUntypedWorkflowStub(workflowId);
            if(workflowId.equals("simpleHumanTask.bpmn")){
                workflowStub.signal("approve", true);
            }

            workflowStub.getResult(String.class);

        }


//        Assert.assertNotNull(result);
//        Assert.assertEquals(result, "my value");
    }
}
