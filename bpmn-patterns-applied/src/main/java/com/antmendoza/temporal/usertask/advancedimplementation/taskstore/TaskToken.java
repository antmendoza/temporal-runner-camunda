package com.antmendoza.temporal.usertask.advancedimplementation.taskstore;

import java.util.StringTokenizer;

public class TaskToken {
  private static final String s = "-@-";
  private final String workflowId;
  private final String suffix;

  public TaskToken(final String workflowId, final String suffix) {
    this.workflowId = workflowId;
    this.suffix = suffix;
  }

  public TaskToken(final String taskToken) {
    StringTokenizer st = new StringTokenizer(taskToken, s);

    this.workflowId = st.nextToken();
    this.suffix = st.nextToken();
  }

  public String getToken() {
    return workflowId + s + suffix;
  }

  public String getWorkflowId() {
    return this.workflowId;
  }
}
