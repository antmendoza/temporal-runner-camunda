package com.antmendoza.temporal.usertask.advancedimplementation.tasks;

import com.antmendoza.temporal.usertask.advancedimplementation.workflow.CompleteTaskRequest;
import io.temporal.workflow.*;
import java.util.List;

@WorkflowInterface
public interface WorkflowTaskManager {

  String WORKFLOW_ID = WorkflowTaskManager.class.getSimpleName();

  @WorkflowMethod
  void run(TasksList taskListService);

  @SignalMethod
  void addTask(Task task);

  @UpdateValidatorMethod(updateName = "completeTask")
  void validateCompleteTask(CompleteTaskRequest changeTaskRequest);

  @UpdateMethod
  void completeTask(CompleteTaskRequest changeTaskRequest);

  @QueryMethod
  List<Task> getPendingTasks(final TaskFilter taskPredicate);
}
