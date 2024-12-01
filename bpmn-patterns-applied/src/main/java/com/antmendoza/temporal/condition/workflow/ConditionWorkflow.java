package com.antmendoza.temporal.condition.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ConditionWorkflow {

  @WorkflowMethod
  String execute(String name);
}
