# POC - Executing Camunda (BPM) processes with Temporal

This project aims to demonstrate how to implement dynamic Temporal workflow to run Camunda BPM processes.

#### The implementation is not completed, below is the list of supported features:

|   | BPM                                    | Temporal implementation                                                            |   
|---|----------------------------------------|------------------------------------------------------------------------------------|
|   | ServiceTask                            | Activity                                                                           |
|   | HumanTask                              | Activity + Dynamic Signal Handler + Workflow.await(()->{})                                 |
|   | HumanTask with boundary timer(timeout) | Activity + Dynamic Signal Handler + Workflow.await(duration, ()->{}) with duration |
|   | Timer                                  | Workflow.sleep                                                                     |

#### Below are some workflows that we have tested with this engine.

##### [simpleActivity.bpmn](src/main/resources/simpleActivity.bpmn)
![Screenshot 2024-11-10 at 21.19.24.png](docs/Screenshot%202024-11-10%20at%2021.19.24.png)

##### [simpleHumanTask.bpmn](src/main/resources/simpleHumanTask.bpmn)
![Screenshot 2024-11-10 at 21.19.29.png](docs/Screenshot%202024-11-10%20at%2021.19.29.png)

##### [simpleHumanTaskWithTimer.bpmn](src/main/resources/simpleHumanTaskWithTimer.bpmn)
![Screenshot 2024-11-10 at 21.19.36.png](docs/Screenshot%202024-11-10%20at%2021.19.36.png)
