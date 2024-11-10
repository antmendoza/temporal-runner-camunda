package com.antmendoza.workflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface Activities {

    @ActivityMethod(name = "com.antmendoza.Activity1")
    String activity1();


    @ActivityMethod(name = "com.antmendoza.Activity2")
    String activity2();

}
