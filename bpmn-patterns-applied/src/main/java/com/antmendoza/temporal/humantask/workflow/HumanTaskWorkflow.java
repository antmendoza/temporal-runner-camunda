package com.antmendoza.temporal.humantask.workflow;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface HumanTaskWorkflow {


    @WorkflowMethod
    String execute(WorkflowInput workflowInput);


    @SignalMethod
    void taskInput(TaskInput taskInput);


    @QueryMethod
    boolean taskCreated();
}
