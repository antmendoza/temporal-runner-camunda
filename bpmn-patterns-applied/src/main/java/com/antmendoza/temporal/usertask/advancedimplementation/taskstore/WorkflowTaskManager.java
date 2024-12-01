package com.antmendoza.temporal.usertask.advancedimplementation.taskstore;

import com.antmendoza.temporal.usertask.advancedimplementation.workflow.ChangeTaskRequest;
import io.temporal.workflow.*;
import java.util.List;

@WorkflowInterface
public interface WorkflowTaskManager {

  String WORKFLOW_ID = WorkflowTaskManager.class.getSimpleName();

  @WorkflowMethod
  void run(TasksList taskListService);

  @SignalMethod
  void addTask(Task task);

  @UpdateValidatorMethod(updateName = "changeTaskStateTo")
  void validateChangeTaskStateTo(ChangeTaskRequest changeTaskRequest);

  @UpdateMethod
  void changeTaskStateTo(ChangeTaskRequest changeTaskRequest);

  @QueryMethod
  List<Task> getAllTasks();
}
