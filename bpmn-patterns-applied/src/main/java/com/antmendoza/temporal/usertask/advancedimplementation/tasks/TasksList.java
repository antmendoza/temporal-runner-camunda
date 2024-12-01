package com.antmendoza.temporal.usertask.advancedimplementation.tasks;

import com.antmendoza.temporal.usertask.advancedimplementation.workflow.CompleteTaskRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
  public Task getTask(final String taskId) {
    return this.allTasks.stream().filter(t -> t.getId().equals(taskId)).findFirst().get();
  }

  @JsonIgnore
  public void completeTask(final CompleteTaskRequest changeTaskRequest) {

    final String taskId = changeTaskRequest.taskId();
    final Task task = getTask(taskId);
    final TaskState taskState = task.getTaskState();
    final TaskState completed = TaskState.Completed;
    if (!task.canTaskTransitionToState(completed)) {
      throw new RuntimeException(
          "Task with id ["
              + taskId
              + "], "
              + "with state ["
              + taskState
              + "], can not transition to "
              + completed);
    }

    this.allTasks.remove(task);
    add(task.withResult(changeTaskRequest.result()).withState(completed));
  }

  @JsonIgnore
  public void timeoutTask(final Task task) {

    final TaskState timeOut = TaskState.TimeOut;
    if (!task.canTaskTransitionToState(timeOut)) {
      throw new RuntimeException(
          "Task with id ["
              + task.getId()
              + "], "
              + "with state ["
              + task.getTaskState()
              + "], can not transition to "
              + timeOut);
    }

    this.allTasks.remove(task);
    add(task.withState(timeOut));
  }

  @JsonIgnore
  public boolean hasUnprocessedTasks() {
    return !this.unprocessedAddedTasks.isEmpty();
  }

  @JsonIgnore
  public Task getNextUnprocessedTask() {
    return unprocessedAddedTasks.remove(unprocessedAddedTasks.size() - 1);
  }

  public List<Task> getAllTasks() {
    return allTasks;
  }

  @JsonIgnore
  public List<Task> getTasks(final Predicate<Task> taskPredicate) {
    return allTasks.stream().filter(taskPredicate).toList();
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
