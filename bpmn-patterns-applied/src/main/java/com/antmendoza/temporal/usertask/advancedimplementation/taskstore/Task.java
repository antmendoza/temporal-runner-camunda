package com.antmendoza.temporal.usertask.advancedimplementation.taskstore;

import com.antmendoza.temporal.usertask.advancedimplementation.workflow.ChangeTaskRequest;
import com.antmendoza.temporal.usertask.advancedimplementation.workflow.TaskState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.lang3.SerializationUtils;


public class Task implements Serializable {
  private String id;
  private String title;
  private String assignedTo;
  private TaskState taskState;

  private Task previousState;
  private String result;

  public Task() {}

  public Task(String id, String assignedTo, String title) {
    this.id = id;
    this.assignedTo = assignedTo;
    this.title = title;
    this.taskState = TaskState.Open;
  }

  private Task(final Builder builder) {
    id = builder.id;
    title = builder.title;
    assignedTo = builder.assignedTo;
    taskState = builder.taskState;
    previousState = builder.previousState;
    result = builder.result;
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

  @JsonIgnore
  public Task withResult(final String result) {
    final Task newTask = SerializationUtils.clone(this);
    newTask.result = result;
    return newTask;
  }

  // Mutate task state with the requested changes
  public void changeTaskState(final ChangeTaskRequest changeTaskRequest) {
    this.previousState = SerializationUtils.clone(this);
    this.taskState = changeTaskRequest.newState();
    this.assignedTo = changeTaskRequest.assignedTo();
    this.result = changeTaskRequest.result();
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
        && Objects.equals(result, task.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, assignedTo, taskState, previousState, result);
  }

  @Override
  public String toString() {
    return "Task{"
        + "id='"
        + id
        + '\''
        + ", title='"
        + title
        + '\''
        + ", assignedTo='"
        + assignedTo
        + '\''
        + ", taskState="
        + taskState
        + ", previousState="
        + previousState
        + ", result='"
        + result
        + '\''
        + '}';
  }


  public static final class Builder {
    private String id;
    private String title;
    private String assignedTo;
    private TaskState taskState;
    private Task previousState;
    private String result;

    public Builder() {
    }

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

    public Task build() {
      return new Task(this);
    }
  }
}
