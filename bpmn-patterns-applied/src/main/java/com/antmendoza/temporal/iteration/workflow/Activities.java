package com.antmendoza.temporal.iteration.workflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface Activities {

  @ActivityMethod
  String prepareOrder(String input);

  @ActivityMethod
  String preparePizza(String input);

  @ActivityMethod
  String bakePizza(String input, int numIterations);

  @ActivityMethod
  String deliverPizza(String input);
}
