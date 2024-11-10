package com.antmendoza.camunda;

import com.antmendoza.jsonmodel.JsonModel;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonMapperTest {


    @Test
    public void parseSimpleActivity(){
        final BpmnModelInstance xml = new CamundaReader().readXML("simpleActivity.bpmn");
        final JsonModel.Root jsonModel = new JsonMapper().toJsonModel(xml);
        Assert.assertNotNull(jsonModel);
    }

    @Test
    public void parseHumanTaskWithTimer(){
        final BpmnModelInstance xml = new CamundaReader().readXML("simpleHumanTaskWithTimer.bpmn");
        final JsonModel.Root jsonModel = new JsonMapper().toJsonModel(xml);
        Assert.assertNotNull(jsonModel);

    }
}