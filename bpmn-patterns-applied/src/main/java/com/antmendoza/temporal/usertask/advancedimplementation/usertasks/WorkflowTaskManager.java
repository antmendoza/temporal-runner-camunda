package com.antmendoza.temporal.usertask.advancedimplementation.usertasks;

import com.antmendoza.temporal.usertask.advancedimplementation.workflow.CompleteTaskRequest;
import io.temporal.workflow.*;
import java.util.List;

@WorkflowInterface
public interface WorkflowTaskManager {

  String WORKFLOW_ID = WorkflowTaskManager.class.getSimpleName();

  @WorkflowMethod
  void run(UserTasksList taskListService);

  @SignalMethod
  void addTask(UserTask userTask);

  @UpdateValidatorMethod(updateName = "completeTask")
  void validateCompleteTask(CompleteTaskRequest changeTaskRequest);

  @UpdateMethod
  void completeTask(CompleteTaskRequest changeTaskRequest);

  @QueryMethod
  List<UserTask> getPendingTasks(final UserTaskFilter taskPredicate);
}
