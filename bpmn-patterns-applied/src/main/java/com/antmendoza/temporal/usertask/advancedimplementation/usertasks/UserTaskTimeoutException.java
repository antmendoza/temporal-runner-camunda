package com.antmendoza.temporal.usertask.advancedimplementation.usertasks;

public class UserTaskTimeoutException extends RuntimeException {
  public UserTaskTimeoutException(final String message) {
    super(message);
  }
}
