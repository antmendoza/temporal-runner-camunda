package com.antmendoza.temporal.usertask.activities;

public class UserTasksImpl implements UserTasks {
    @Override
    public String createTasks(final String userId) {
        //In real word implementation task is created in an external system, it can be a workflow
        return "taskId:" + Math.random();
    }
}
