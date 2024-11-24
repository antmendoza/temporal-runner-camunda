package com.antmendoza.camunda;

import com.antmendoza.jsonmodel.JsonModel;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class JsonMapper {


    public JsonMapper() {

    }

    private static void addNextStep(
            final BpmnModelInstance modelInstance,
            final FlowNode step, final ArrayList<JsonModel.Step> steps, final ArrayList<JsonModel.Action> actions) {
        FlowNode activity = step.getOutgoing().iterator().next().getTarget();

        if (!(activity instanceof StartEvent) && !(activity instanceof EndEvent)
        ) {
            addStep(modelInstance, activity, steps, actions);
        }


        if (activity.getOutgoing().iterator().hasNext()) {
            addNextStep(modelInstance,activity, steps, actions);
        }

    }

    private static void addStep(final BpmnModelInstance modelInstance,
            final FlowNode step,
            final ArrayList<JsonModel.Step> steps,
            final ArrayList<JsonModel.Action> actions) {

        final JsonModel.Step e = new JsonModel.Step();
        e.actionName = step.getName();
        e.input = null;

        final JsonModel.Action action = new JsonModel.Action();

        action.type = extractType(step);
        action.name = extractName(step);
        action.startToCloseTimeoutMs = extractStartToCloseTimeout(step);
        action.activityName = extractActivityName(step);
        action.sleepMs = extractSleepMs(step);
        action.waitTimeoutMs = extractWaitTimeout(modelInstance, step);

        if (!(step instanceof StartEvent)) {
            steps.add(e);
            actions.add(action);

        } else {
            System.out.println("Ignoring start event.");
        }


    }

    private static int extractWaitTimeout(final BpmnModelInstance modelInstance, final FlowNode step) {


        if(step instanceof UserTask){

            final Collection<BoundaryEvent> modelElementsByType = modelInstance.getModelElementsByType(BoundaryEvent.class);
            if(modelElementsByType != null
                    && !modelElementsByType.isEmpty()){
                
                    //fin boundary events in bpm model
                Optional<BoundaryEvent> attachedTimer = modelElementsByType.stream().filter(e -> e.getAttachedTo().getId().equals(step.getId())).findFirst();

                if(attachedTimer.isPresent()){
                    return Integer.parseInt(attachedTimer.get()
                            .getChildElementsByType(TimerEventDefinition.class)
                            .iterator().next().getTimeDuration()
                            .getTextContent());
                }
            }

        }



        return 0;
    }

    private static String extractName(final FlowNode step) {
        return step.getName();
    }

    private static int extractSleepMs(final FlowNode step) {

        if (step instanceof IntermediateCatchEvent) {

            return Integer.parseInt(step
                    .getChildElementsByType(TimerEventDefinition.class)
                    .iterator().next().getTimeDuration()
                    .getTextContent());


//            String name = step.getName();
//
//            if (name.endsWith("ms")) {
//                return Integer.parseInt(name.substring(0, name.length() - 2));
//            }
//            if (name.endsWith("s")) {
//                return Integer.parseInt(name.substring(0, name.length() - 1)) * 1000;
//            }
        }

        return 0;
    }

    private static String extractActivityName(final FlowNode step) {

        if (step instanceof ServiceTask) {
            return ((ServiceTask) step).getCamundaClass();
        }
        return null;

    }

    private static int extractStartToCloseTimeout(final FlowNode step) {
        if (step instanceof ServiceTask) {


            //TODO
            return 5000;
        }

        return 0;
    }

    private static String extractType(final FlowNode step) {
        if (step instanceof ServiceTask) {
            return "executeActivity";
        }


        if (step instanceof IntermediateCatchEvent) {
            return "sleep";
        }

        if (step instanceof UserTask) {
            return "userTask";
        }

        return null;
    }

    public JsonModel.Root toJsonModel(final BpmnModelInstance modelInstance) {
        JsonModel.Root root = new JsonModel.Root();
        final ArrayList<JsonModel.Step> steps = new ArrayList<>();
        final ArrayList<JsonModel.Action> actions = new ArrayList<>();


        Collection<FlowNode> tasks = modelInstance.getModelElementsByType(FlowNode.class);
        for (FlowNode task : tasks) {

            System.out.println("Task Class: " + task.getClass());
            System.out.println("Task ID: " + task.getId());
            System.out.println("Task Name: " + task.getName());

        }

        FlowNode step = modelInstance.getModelElementsByType(StartEvent.class).stream().toList().get(0);

        addNextStep(modelInstance, step, steps, actions);

        root.steps = steps;
        root.actions = actions;
        root.queries = new ArrayList<>();


        return root;
    }


}
