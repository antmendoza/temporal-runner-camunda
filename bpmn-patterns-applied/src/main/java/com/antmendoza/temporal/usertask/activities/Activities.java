package com.antmendoza.temporal.usertask.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface Activities {

  @ActivityMethod
  String activity1(String input);

  @ActivityMethod
  String activity2(String input);

  @ActivityMethod
  String activity3(String input);

  @ActivityMethod
  String activity4(String input);
}
