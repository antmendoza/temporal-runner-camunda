package com.antmendoza.temporal.iteration.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface IterationWorkflow {


    @WorkflowMethod
    String execute(String name);
}
