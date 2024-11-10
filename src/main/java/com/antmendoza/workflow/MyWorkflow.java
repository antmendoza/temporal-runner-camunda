package com.antmendoza.workflow;

import com.antmendoza.jsonmodel.JsonModel;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MyWorkflow {


    @WorkflowMethod
    String run(JsonModel.Root flow, WorkflowInput input);
}
