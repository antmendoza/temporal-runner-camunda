package com.antmendoza.temporal.usertask.advancedimplementation.taskstore;

import com.antmendoza.temporal.usertask.advancedimplementation.workflow.TaskState;

import java.util.function.Predicate;

public class TaskFilter implements Predicate<Task> {


    public static TaskFilter OPEN_TASKS = new TaskFilter(new Task.Builder().taskState(TaskState.Open).build());

    private Task t;

    public TaskFilter() {
    }

    public TaskFilter(Task t) {
        this.t = t;

    }


    @Override
    public boolean test(final Task task) {
        if (t.getTaskState() != null && !t.getTaskState().equals(task.getTaskState()) ) {
            return false;
        }
        return true;
    }


}
