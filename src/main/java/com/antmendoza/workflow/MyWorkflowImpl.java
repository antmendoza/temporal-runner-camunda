package com.antmendoza.workflow;

import com.antmendoza.jsonmodel.JsonModel;
import io.temporal.activity.ActivityOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.ActivityStub;
import io.temporal.workflow.DynamicQueryHandler;
import io.temporal.workflow.DynamicSignalHandler;
import io.temporal.workflow.Workflow;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyWorkflowImpl implements MyWorkflow {
    @Override
    public String run(final JsonModel.Root flow, final WorkflowInput input) {

        if (flow == null) {
            throw ApplicationFailure.newFailure("flow is null", "NullFlowException");
        }

        flow.queries.forEach(
                (query -> {
                    Workflow.registerListener(
                            (DynamicQueryHandler)
                                    (queryType, args) -> {
                                        final JsonModel.QueryExecutor queryExecutor = query.queryExecutor;
                                        if (queryExecutor != null) {
                                            try {
                                                Class cl = Class.forName(queryExecutor.className);
                                                Object o = cl.getConstructor(null).newInstance(null);
                                                Method m = cl.getMethod(queryExecutor.methodName, null);
                                                return m.invoke(o);

                                            } catch (Exception e) {
                                                // if this should throw a non retryable error
                                                throw new RuntimeException(e);
                                            }
                                        }
                                        throw ApplicationFailure.newFailure(
                                                "scenario not handled", "QueryHandlerException");
                                    });
                }));

        final String[] result = {""};

        flow.steps.forEach(
                step -> {
                    final JsonModel.Action action =
                            flow.actions.stream().filter(a -> a.name.equals(step.actionName)).findFirst().get();

                    if (action.type.equals("executeActivity")) {

                        // Execute activity
                        ActivityStub activities =
                                Workflow.newUntypedActivityStub(
                                        ActivityOptions.newBuilder()
                                                .setStartToCloseTimeout(Duration.ofMillis(action.startToCloseTimeoutMs))
                                                .build());

                        // Append result to #result
                        final String execute =
                                activities.execute(action.activityName, String.class, step.input);
                        result[0] += execute; // TODO review String.class as returned type
                    }


                    if (action.type.equals("userTask")) {

                        final AtomicBoolean approved = new AtomicBoolean(false);

                        Workflow.registerListener(
                                (DynamicSignalHandler)
                                        (signalName, encodedArgs) -> {
                                            if (signalName.equals("approve")) {
                                                approved.set(true);
                                            }
                                        });


                        if (action.waitTimeoutMs > 0) {
                            Workflow.await(Duration.ofMillis(action.waitTimeoutMs), () -> approved.get());
                        } else {

                            Workflow.await(() -> approved.get());
                        }
                    }


                    if (action.type.equals("sleep")) {
                        Workflow.sleep(action.sleepMs);
                    }
                });

        return result[0];
    }

}
