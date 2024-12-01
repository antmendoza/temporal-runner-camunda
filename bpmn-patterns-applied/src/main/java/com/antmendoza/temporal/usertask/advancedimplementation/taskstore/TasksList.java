package com.antmendoza.temporal.usertask.advancedimplementation.taskstore;

import com.antmendoza.temporal.usertask.advancedimplementation.workflow.ChangeTaskRequest;
import com.antmendoza.temporal.usertask.advancedimplementation.workflow.TaskState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TasksList {

  private final List<Task> allTasks = new ArrayList<>();

  // Task whin changes that has not been processed by the workflow yet.
  private final List<Task> unprocessedAddedTasks = new ArrayList<>();

  public void addAll(final TasksList taskList) {
    this.unprocessedAddedTasks.addAll(
        taskList != null ? taskList.unprocessedAddedTasks : new ArrayList<>());
    this.allTasks.addAll(taskList != null ? taskList.allTasks : new ArrayList<>());
  }

  public TasksList() {}

  @JsonIgnore
  public void add(final Task task) {
    this.unprocessedAddedTasks.add(task);
    this.allTasks.add(task);
  }

  @JsonIgnore
  public List<Task> getAll() {
    return this.allTasks;
  }

  @JsonIgnore
  public boolean canTaskTransitionToState(final ChangeTaskRequest changeTaskRequest) {
    final TaskState taskState = getTask(changeTaskRequest.taskId()).getTaskState();

    boolean canTransitionate = false;
    final TaskState newState = changeTaskRequest.newState();
    switch (newState) {
      case Completed:
        if (taskState.equals(TaskState.New)) {
          canTransitionate = true;
        }
        break;

        // TODO implement validation for other transitions
      default:
          throw new RuntimeException(taskState + " to " + newState + " not implemented");
    }

    return canTransitionate;
  }

  @JsonIgnore
  public Task getTask(final String taskId) {
    return this.allTasks.stream().filter(t -> t.getId().equals(taskId)).findFirst().get();
  }

  @JsonIgnore
  public void changeTaskStateTo(final ChangeTaskRequest changeTaskRequest) {

    final String taskId = changeTaskRequest.taskId();
    final Task task = getTask(taskId);
    final TaskState taskState = task.getTaskState();
    if (!canTaskTransitionToState(changeTaskRequest)) {
      throw new RuntimeException(
          "Task with id ["
              + taskId
              + "], "
              + "with state ["
              + taskState
              + "], can not transition to "
              + changeTaskRequest.newState());
    }

    // For the sake of simplicity Task is mutable
    task.changeTaskState(changeTaskRequest);
    this.unprocessedAddedTasks.add(task);
  }

  @JsonIgnore
  public boolean hasUnprocessedTasks() {
    return !this.unprocessedAddedTasks.isEmpty();
  }

  @JsonIgnore
  public Task getNextUnprocessedTasks() {
    return unprocessedAddedTasks.remove(unprocessedAddedTasks.size() - 1);
  }

  public List<Task> getAllTasks() {
    return allTasks;
  }


  public List<Task> getPendingTasks() {
    return allTasks.stream().filter(t -> !t.getTaskState().equals(TaskState.Completed)).toList();
  }

  @Override
  public String toString() {
    return "TasksList{"
        + "tasks="
        + allTasks
        + ", unprocessedAddedTasks="
        + unprocessedAddedTasks
        + '}';
  }
}
