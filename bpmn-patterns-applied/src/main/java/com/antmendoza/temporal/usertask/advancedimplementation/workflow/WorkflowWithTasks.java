package com.antmendoza.temporal.usertask.advancedimplementation.workflow;

import com.antmendoza.temporal.usertask.advancedimplementation.usertasks.UserTaskToken;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInfo;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface WorkflowWithTasks {

  @WorkflowMethod
  String execute();

  class GenerateTaskToken {
    private int taskToken = 1;

    public String getNext() {
      final WorkflowInfo info = Workflow.getInfo();
      final String workflowId = info.getWorkflowId();
      return new UserTaskToken(workflowId, Workflow.currentTimeMillis() + ":" + taskToken++)
          .getToken();
    }
  }
}
