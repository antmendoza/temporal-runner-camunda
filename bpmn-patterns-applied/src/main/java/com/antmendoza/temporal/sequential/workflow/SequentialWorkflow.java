package com.antmendoza.temporal.sequential.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SequentialWorkflow {


    @WorkflowMethod
    String execute(String name);
}
