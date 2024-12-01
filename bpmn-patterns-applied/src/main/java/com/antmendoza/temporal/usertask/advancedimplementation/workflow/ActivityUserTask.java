package com.antmendoza.temporal.usertask.advancedimplementation.workflow;

import com.antmendoza.temporal.usertask.advancedimplementation.usertasks.UserTask;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface ActivityUserTask {

  void createTask(UserTask userTask);
}
