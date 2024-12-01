package com.antmendoza.temporal.usertask.advancedimplementation.workflow;

import java.util.Objects;

public final class CompleteTaskRequest {
  private String taskId;
  private String result;

  public CompleteTaskRequest() {}

  public CompleteTaskRequest(String taskId, String result) {
    this.taskId = taskId;
    this.result = result;
  }

  public String taskId() {
    return taskId;
  }

  public String result() {
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (CompleteTaskRequest) obj;
    return Objects.equals(this.taskId, that.taskId) && Objects.equals(this.result, that.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taskId, result);
  }

  @Override
  public String toString() {
    return "ChangeTaskRequest[" + "taskId=" + taskId + ", " + "result=" + result + ']';
  }
}
