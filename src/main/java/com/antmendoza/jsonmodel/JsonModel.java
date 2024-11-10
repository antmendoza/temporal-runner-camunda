package com.antmendoza.jsonmodel;

import java.util.ArrayList;

public class JsonModel {
  public static class Action {
    public String name;
    public String type;
    public int startToCloseTimeoutMs;
    public String activityName;
    public int sleepMs;
    public int waitTimeoutMs;
  }

  public static class Query {
    public QueryExecutor queryExecutor;
  }

  public static class QueryExecutor {
    public String className;
    public String methodName;
  }

  public static class Root {
    public ArrayList<Query> queries;
    public ArrayList<Action> actions;
    public ArrayList<Step> steps;
  }

  public static class Step {
    public String actionName;
    public String input;
  }
}
