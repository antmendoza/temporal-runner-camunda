package com.antmendoza.temporal.humantask.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface UserTasks {

    @ActivityMethod
    String createTasks(String userId);

}
