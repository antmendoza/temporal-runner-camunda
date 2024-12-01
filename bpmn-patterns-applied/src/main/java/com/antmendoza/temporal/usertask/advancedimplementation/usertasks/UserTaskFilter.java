package com.antmendoza.temporal.usertask.advancedimplementation.usertasks;

import java.util.function.Predicate;

public class UserTaskFilter implements Predicate<UserTask> {

  public static UserTaskFilter OPEN_TASKS =
      new UserTaskFilter(new UserTask.Builder().taskState(UserTaskState.Open).build());

  private UserTask t;

  public UserTaskFilter() {}

  public UserTaskFilter(UserTask t) {
    this.t = t;
  }

  @Override
  public boolean test(final UserTask userTask) {
    if (t.getUserTaskState() != null && !t.getUserTaskState().equals(userTask.getUserTaskState())) {
      return false;
    }
    return true;
  }
}
