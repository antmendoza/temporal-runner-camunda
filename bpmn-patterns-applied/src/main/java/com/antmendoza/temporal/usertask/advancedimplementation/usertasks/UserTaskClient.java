package com.antmendoza.temporal.usertask.advancedimplementation.usertasks;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;

@WorkflowInterface
public interface UserTaskClient {

  @SignalMethod
  void completeTaskByToken(final String taskToken, final String result);

  @SignalMethod
  void timeOutTaskByToken(final String taskToken);
}
