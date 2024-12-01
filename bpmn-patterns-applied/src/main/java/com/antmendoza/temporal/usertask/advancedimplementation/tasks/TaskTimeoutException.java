package com.antmendoza.temporal.usertask.advancedimplementation.tasks;

public class TaskTimeoutException extends RuntimeException {
  public TaskTimeoutException(final String message) {
    super(message);
  }
}
