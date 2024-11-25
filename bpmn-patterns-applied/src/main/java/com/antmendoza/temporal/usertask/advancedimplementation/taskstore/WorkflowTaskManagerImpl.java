package com.antmendoza.temporal.usertask.advancedimplementation.taskstore;

import com.antmendoza.temporal.usertask.advancedimplementation.workflow.*;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;

public class WorkflowTaskManagerImpl implements WorkflowTaskManager {
    private final Logger logger = Workflow.getLogger(WorkflowTaskManagerImpl.class.getName());

    private final TasksList taskListService = new TasksList();

    private void completeTasks(final Task task) {
        try {

            final String taskId = task.getId();
            final String externalWorkflowId = new TaskToken(taskId).getWorkflowId();

            logger.debug("taskId: " + taskId);
            logger.debug("externalWorkflowId: " + externalWorkflowId);
            Workflow.newExternalWorkflowStub(TaskClient.class, externalWorkflowId).completeTaskByToken(
                    taskId, task.getResult()
            );
        } catch (ApplicationFailure e) {
            throw e;

        }
    }

    @Override
    public void run(TasksList taskList) {

        this.taskListService.addAll(taskList);


        while (true) {

            Workflow.await(
                    () ->
                            // Wait until there are pending task to process
                            this.taskListService.hasUnprocessedTasks());

            final Task task = this.taskListService.getNextUnprocessedTasks();
            logger.info("Processing task " + task);



            Task previousTask = task.getPreviousState();
            logger.info("Processing previousTask " + previousTask);
            // notify the user...
            if (previousTask != null
                    && !Objects.equals(task.getAssignedTo(), previousTask.getAssignedTo())) {
                // Notify use task.getAssignedTo()
            }

            if (task.getTaskState().equals(TaskState.Completed)) {
                completeTasks(task);
            }

        }

        // TODO
        // Workflow.continueAsNew();


    }

    @Override
    public void addTask(Task task) {

        this.taskListService.add(task);


    }

    @Override
    public void validateChangeTaskStateTo(ChangeTaskRequest changeTaskRequest) {

        final String taskId = changeTaskRequest.taskId();
        if (!taskListService.canTaskTransitionToState(changeTaskRequest)) {
            final TaskState taskState = taskListService.getTask(taskId).getTaskState();
            throw new RuntimeException(
                    "Task with id ["
                            + taskId
                            + "], "
                            + "with state ["
                            + taskState
                            + "], can not transition to "
                            + changeTaskRequest.newState());
        }
    }

    @Override
    public void changeTaskStateTo(ChangeTaskRequest changeTaskRequest) {
        taskListService.changeTaskStateTo(changeTaskRequest);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskListService.getTasks();
    }
}
