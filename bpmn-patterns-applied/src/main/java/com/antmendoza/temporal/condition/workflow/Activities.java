package com.antmendoza.temporal.condition.workflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface Activities {

    @ActivityMethod
    int calculateX(String input);

    @ActivityMethod
    String x_greater_than_5(String input);

    @ActivityMethod
    String x_less_than_or_equal_to_5(String input);
}
