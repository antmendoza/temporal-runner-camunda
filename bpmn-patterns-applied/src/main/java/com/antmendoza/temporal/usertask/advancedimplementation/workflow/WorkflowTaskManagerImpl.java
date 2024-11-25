package com.antmendoza.temporal.usertask.advancedimplementation.workflow;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;

public class WorkflowTaskManagerImpl implements WorkflowTaskManager {
  private final Logger logger = Workflow.getLogger(WorkflowTaskManagerImpl.class.getName());

  private TasksList taskListService;

  @Override
  public void run(TasksList taskList) {

    initTaskList(taskList);


    System.out.println("taskListService" + taskListService);

    while (true) {

      Workflow.await(
          () ->
              // Wait until there are pending task to process
              this.taskListService.hasUnprocessedTasks());

      final Task task = this.taskListService.getNextUnprocessedTasks();
      logger.info("Processing task " + task);
      Task previousTask = task.getPreviousState();
      logger.info("Processing previousTask " + previousTask);

      // Here we could add activities to notify the user...
      if (previousTask != null
          && !Objects.equals(task.getAssignedTo(), previousTask.getAssignedTo())) {
        // Notify use task.getAssignedTo()
      }
    }
  }

  private void initTaskList(final TasksList taskList) {


    this.taskListService = new TasksList(taskList);



  }

  @Override
  public void addTask(Task task) {

    initTaskList(null);


    taskListService.add(task);
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
