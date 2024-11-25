package com.antmendoza.temporal.usertask.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface UserTasks {

    @ActivityMethod
    String createTasks(String userId);

}
