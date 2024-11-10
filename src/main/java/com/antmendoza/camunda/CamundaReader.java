package com.antmendoza.camunda;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class CamundaReader {
    public BpmnModelInstance readXML(final String filename) {
        try {

            ClassLoader classLoader = getClass().getClassLoader();

            // Load the BPMN XML file
            File file = new File(classLoader.getResource(filename).getFile());
            InputStream inputStream = new FileInputStream(file);

            // Parse the file into a BpmnModelInstance
            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(inputStream);


            return modelInstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
