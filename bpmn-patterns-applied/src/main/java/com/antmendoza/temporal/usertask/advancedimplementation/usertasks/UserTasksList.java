package com.antmendoza.temporal.usertask.advancedimplementation.usertasks;

import com.antmendoza.temporal.usertask.advancedimplementation.workflow.CompleteTaskRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class UserTasksList {

  private final List<UserTask> allUserTasks = new ArrayList<>();

  // Task whin changes that has not been processed by the workflow yet.
  private final List<UserTask> unprocessedAddedUserTasks = new ArrayList<>();

  public void addAll(final UserTasksList taskList) {
    this.unprocessedAddedUserTasks.addAll(
        taskList != null ? taskList.unprocessedAddedUserTasks : new ArrayList<>());
    this.allUserTasks.addAll(taskList != null ? taskList.allUserTasks : new ArrayList<>());
  }

  public UserTasksList() {}

  @JsonIgnore
  public void add(final UserTask userTask) {
    this.unprocessedAddedUserTasks.add(userTask);
    this.allUserTasks.add(userTask);
  }

  @JsonIgnore
  public List<UserTask> getAll() {
    return this.allUserTasks;
  }

  @JsonIgnore
  public UserTask getTask(final String taskId) {
    return this.allUserTasks.stream().filter(t -> t.getId().equals(taskId)).findFirst().get();
  }

  @JsonIgnore
  public void completeTask(final CompleteTaskRequest changeTaskRequest) {

    final String taskId = changeTaskRequest.taskId();
    final UserTask userTask = getTask(taskId);
    final UserTaskState userTaskState = userTask.getUserTaskState();
    final UserTaskState completed = UserTaskState.Completed;
    if (!userTask.canTaskTransitionToState(completed)) {
      throw new RuntimeException(
          "Task with id ["
              + taskId
              + "], "
              + "with state ["
              + userTaskState
              + "], can not transition to "
              + completed);
    }

    this.allUserTasks.remove(userTask);
    add(userTask.withResult(changeTaskRequest.result()).withState(completed));
  }

  @JsonIgnore
  public void timeoutTask(final UserTask userTask) {

    final UserTaskState timeOut = UserTaskState.TimeOut;
    if (!userTask.canTaskTransitionToState(timeOut)) {
      throw new RuntimeException(
          "Task with id ["
              + userTask.getId()
              + "], "
              + "with state ["
              + userTask.getUserTaskState()
              + "], can not transition to "
              + timeOut);
    }

    this.allUserTasks.remove(userTask);
    add(userTask.withState(timeOut));
  }

  @JsonIgnore
  public boolean hasUnprocessedTasks() {
    return !this.unprocessedAddedUserTasks.isEmpty();
  }

  @JsonIgnore
  public UserTask getNextUnprocessedTask() {
    return unprocessedAddedUserTasks.remove(unprocessedAddedUserTasks.size() - 1);
  }

  public List<UserTask> getAllTasks() {
    return allUserTasks;
  }

  @JsonIgnore
  public List<UserTask> getTasks(final Predicate<UserTask> taskPredicate) {
    return allUserTasks.stream().filter(taskPredicate).toList();
  }

  @Override
  public String toString() {
    return "TasksList{"
        + "tasks="
        + allUserTasks
        + ", unprocessedAddedTasks="
        + unprocessedAddedUserTasks
        + '}';
  }
}
