package com.antmendoza.temporal.usertask.advancedimplementation.usertasks;

import java.util.StringTokenizer;

public class UserTaskToken {
  private static final String s = "::@::";
  private final String workflowId;
  private final String uniqueWorkflowToken;

  public UserTaskToken(final String workflowId, final String uniqueWorkflowToken) {
    this.workflowId = workflowId;
    this.uniqueWorkflowToken = uniqueWorkflowToken;
  }

  public UserTaskToken(final String taskToken) {
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
