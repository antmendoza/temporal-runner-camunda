package com.antmendoza.temporal.usertask.advancedimplementation.tasks;

import java.util.StringTokenizer;

public class TaskToken {
  private static final String s = "::@::";
  private final String workflowId;
  private final String uniqueWorkflowToken;

  public TaskToken(final String workflowId, final String uniqueWorkflowToken) {
    this.workflowId = workflowId;
    this.uniqueWorkflowToken = uniqueWorkflowToken;
  }

  public TaskToken(final String taskToken) {
    StringTokenizer st = new StringTokenizer(taskToken, s);

    this.workflowId = st.nextToken();
    this.uniqueWorkflowToken = st.nextToken();
  }

  public String getToken() {
    return workflowId + s + uniqueWorkflowToken;
  }

  public String getWorkflowId() {
    return this.workflowId;
  }
}
