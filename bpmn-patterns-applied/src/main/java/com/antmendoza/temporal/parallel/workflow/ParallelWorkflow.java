package com.antmendoza.temporal.parallel.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ParallelWorkflow {


    @WorkflowMethod
    String execute(String name);
}
