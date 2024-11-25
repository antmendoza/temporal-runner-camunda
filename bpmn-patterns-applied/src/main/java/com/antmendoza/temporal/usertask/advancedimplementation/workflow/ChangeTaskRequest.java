package com.antmendoza.temporal.usertask.advancedimplementation.workflow;

public record ChangeTaskRequest(String taskId,
                                String assignedTo,
                                String candidate,
                                TaskState newState) {

}
