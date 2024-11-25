package com.antmendoza.temporal.usertask.advancedimplementation.workflow;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Objects;

public class Task implements Serializable {
    private String id;
    private String title;
    private String assignedTo;
    private TaskState taskState;

    private Task previousState;
    private String result;

    public Task() {
    }

    public Task(String id, String title) {
        this.id = id;
        this.title = title;
        this.taskState = TaskState.New;
    }

    public String getTitle() {
        return title;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public String getId() {
        return id;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public Task getPreviousState() {
        return previousState;
    }

    public String getResult() {
        return result;
    }

    // Mutate task state with the requested changes
    public void changeTaskState(final ChangeTaskRequest changeTaskRequest) {
        this.previousState = SerializationUtils.clone(this);
        this.taskState = changeTaskRequest.newState();
        this.assignedTo = changeTaskRequest.assignedTo();
        this.result = changeTaskRequest.result();
    }


    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(title, task.title) && Objects.equals(assignedTo, task.assignedTo) && taskState == task.taskState && Objects.equals(previousState, task.previousState)
                && Objects.equals(result, task.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, assignedTo, taskState, previousState, result);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", assignedTo='" + assignedTo + '\'' +
                ", taskState=" + taskState +
                ", previousState=" + previousState +
                ", result='" + result + '\'' +
                '}';
    }
}
