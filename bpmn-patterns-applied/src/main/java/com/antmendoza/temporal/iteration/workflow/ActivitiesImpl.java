package com.antmendoza.temporal.iteration.workflow;

import com.thedeanda.lorem.LoremIpsum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivitiesImpl implements Activities {
  private static final Logger log = LoggerFactory.getLogger(ActivitiesImpl.class);

  @Override
  public String prepareOrder(final String input) {
    return LoremIpsum.getInstance().getTitle(10);
  }

  @Override
  public String preparePizza(final String input) {
    return LoremIpsum.getInstance().getTitle(10);
  }

  @Override
  public String bakePizza(final String input, int workflowIterations) {

    // Simulate pizza is ok after 3 iterations
    if (workflowIterations == 3) {
      return "qualityOK";
    }
    return LoremIpsum.getInstance().getTitle(10);
  }

  @Override
  public String deliverPizza(final String input) {
    return LoremIpsum.getInstance().getTitle(10);
  }
}
