package com.antmendoza.temporal.humantask.activities;

import com.thedeanda.lorem.LoremIpsum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivitiesImpl implements Activities {
    private static final Logger log = LoggerFactory.getLogger(ActivitiesImpl.class);


    @Override
    public String activity1(final String input) {
        return LoremIpsum.getInstance().getTitle(10);
    }

    @Override
    public String activity2(final String input) {

        return LoremIpsum.getInstance().getTitle(10);

    }

    @Override
    public String activity3(final String input) {
        return LoremIpsum.getInstance().getTitle(10);
    }
}
