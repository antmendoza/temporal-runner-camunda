package com.antmendoza.temporal.usertask.basicimplementation.workflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface UserTasks {

  @ActivityMethod
  String createTasks(String userId);
}