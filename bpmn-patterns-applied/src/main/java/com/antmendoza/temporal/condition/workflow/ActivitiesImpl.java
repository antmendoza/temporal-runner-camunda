package com.antmendoza.temporal.condition.workflow;

import io.temporal.activity.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class ActivitiesImpl implements Activities {
    private static final Logger log = LoggerFactory.getLogger(ActivitiesImpl.class);


    @Override
    public int calculateX(final String input) {
        return ThreadLocalRandom.current().nextInt(0, 10 + 1);
    }

    @Override
    public String x_greater_than_5(final String input) {
        return Activity.getExecutionContext().getInfo().getActivityType()  + " executed";
    }

    @Override
    public String x_less_than_or_equal_to_5(final String input) {
        return Activity.getExecutionContext().getInfo().getActivityType()  + " executed";
    }
}
