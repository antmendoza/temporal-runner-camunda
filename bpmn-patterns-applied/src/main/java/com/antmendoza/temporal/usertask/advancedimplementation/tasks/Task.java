package com.antmendoza.temporal.usertask.advancedimplementation.tasks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;
import org.apache.commons.lang3.SerializationUtils;

public class Task implements Serializable {
  private String id;
  private String title;
  private String assignedTo;
  private TaskState taskState;

  private Task previousState;
  private String result;
  private Duration deadline;

  public Task() {
    taskState = TaskState.Open;
  }

  private Task(final Builder builder) {
    id = builder.id;
    title = builder.title;
    assignedTo = builder.assignedTo;
    taskState = builder.taskState == null ? TaskState.Open : builder.taskState;
    previousState = builder.previousState;
    result = builder.result;
    deadline = builder.deadline;
  }

  public String getTitle() {
    return title;
  }

  public TaskState getTaskState() {
    return taskState;
  }

  public String getId() {
    return id;
  }

  public String getAssignedTo() {
    return assignedTo;
  }

  public Task getPreviousState() {
    return previousState;
  }

  public String getResult() {
    return result;
  }

  public Duration getDeadline() {
    return deadline;
  }

  @JsonIgnore
  public Task withResult(final String result) {
    final Task newTask = cloneTaskAndStorePreviousState();
    newTask.result = result;
    return newTask;
  }


  @JsonIgnore
  public Task withState(final TaskState taskState) {
    final Task newTask = cloneTaskAndStorePreviousState();
    newTask.taskState = taskState;
    return newTask;
  }

  private Task cloneTaskAndStorePreviousState() {
    final Task clone = SerializationUtils.clone(this);
    clone.previousState = previousState;
    return clone;
  }


  @Override
  public boolean equals(final Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    final Task task = (Task) o;
    return Objects.equals(id, task.id)
        && Objects.equals(title, task.title)
        && Objects.equals(assignedTo, task.assignedTo)
        && taskState == task.taskState
        && Objects.equals(previousState, task.previousState)
        && Objects.equals(result, task.result)
        && Objects.equals(deadline, task.deadline);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, assignedTo, taskState, previousState, result, deadline);
  }

  @JsonIgnore
  public boolean canTaskTransitionToState(final TaskState newState) {

    boolean canTransitionate = false;
    switch (newState) {
      case Completed, TimeOut:
        if (taskState.equals(TaskState.Open)) {
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
    return getTaskState().equals(TaskState.Completed)
            || getTaskState().equals(TaskState.TimeOut);
  }


  public static final class Builder {
    private String id;
    private String title;
    private String assignedTo;
    private TaskState taskState;
    private Task previousState;
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

    public Builder taskState(final TaskState val) {
      taskState = val;
      return this;
    }

    public Builder previousState(final Task val) {
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

    public Task build() {
      return new Task(this);
    }
  }
}
