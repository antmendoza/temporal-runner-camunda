package com.antmendoza.temporal.usertask.advancedimplementation.usertasks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;
import org.apache.commons.lang3.SerializationUtils;

public class UserTask implements Serializable {
  private String id;
  private String title;
  private String assignedTo;
  private UserTaskState userTaskState;

  private UserTask previousState;
  private String result;
  private Duration deadline;

  public UserTask() {
    userTaskState = UserTaskState.Open;
  }

  private UserTask(final Builder builder) {
    id = builder.id;
    title = builder.title;
    assignedTo = builder.assignedTo;
    userTaskState = builder.userTaskState == null ? UserTaskState.Open : builder.userTaskState;
    previousState = builder.previousState;
    result = builder.result;
    deadline = builder.deadline;
  }

  public String getTitle() {
    return title;
  }

  public UserTaskState getUserTaskState() {
    return userTaskState;
  }

  public String getId() {
    return id;
  }

  public String getAssignedTo() {
    return assignedTo;
  }

  public UserTask getPreviousState() {
    return previousState;
  }

  public String getResult() {
    return result;
  }

  public Duration getDeadline() {
    return deadline;
  }

  @JsonIgnore
  public UserTask withResult(final String result) {
    final UserTask newUserTask = cloneTaskAndStorePreviousState();
    newUserTask.result = result;
    return newUserTask;
  }


  @JsonIgnore
  UserTask withState(final UserTaskState userTaskState) {
    final UserTask newUserTask = cloneTaskAndStorePreviousState();
    newUserTask.userTaskState = userTaskState;
    return newUserTask;
  }

  private UserTask cloneTaskAndStorePreviousState() {
    final UserTask clone = SerializationUtils.clone(this);
    clone.previousState = previousState;
    return clone;
  }


  @Override
  public boolean equals(final Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    final UserTask userTask = (UserTask) o;
    return Objects.equals(id, userTask.id)
        && Objects.equals(title, userTask.title)
        && Objects.equals(assignedTo, userTask.assignedTo)
        && userTaskState == userTask.userTaskState
        && Objects.equals(previousState, userTask.previousState)
        && Objects.equals(result, userTask.result)
        && Objects.equals(deadline, userTask.deadline);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, assignedTo, userTaskState, previousState, result, deadline);
  }

  @JsonIgnore
  public boolean canTaskTransitionToState(final UserTaskState newState) {

    boolean canTransitionate = false;
    switch (newState) {
      case Completed, TimeOut:
        if (userTaskState.equals(UserTaskState.Open)) {
          canTransitionate = true;
        }
        break;

        // TODO implement validation for other transitions
      default:
        canTransitionate = false;
        //throw new RuntimeException(taskState + " to " + newState + " not implemented");
    }

    return canTransitionate;
  }

  @JsonIgnore
  public boolean isClosed() {
    return getUserTaskState().equals(UserTaskState.Completed)
            || getUserTaskState().equals(UserTaskState.TimeOut);
  }


  public static final class Builder {
    private String id;
    private String title;
    private String assignedTo;
    private UserTaskState userTaskState;
    private UserTask previousState;
    private String result;
    private Duration deadline;

    public Builder() {}

    public Builder id(final String val) {
      id = val;
      return this;
    }

    public Builder title(final String val) {
      title = val;
      return this;
    }

    public Builder assignedTo(final String val) {
      assignedTo = val;
      return this;
    }

    public Builder taskState(final UserTaskState val) {
      userTaskState = val;
      return this;
    }

    public Builder previousState(final UserTask val) {
      previousState = val;
      return this;
    }

    public Builder result(final String val) {
      result = val;
      return this;
    }

    public Builder deadline(final Duration val) {
      deadline = val;
      return this;
    }

    public UserTask build() {
      return new UserTask(this);
    }
  }
}
