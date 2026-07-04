package xw.flow.constants;

public enum FlowNodeType {
    startEvent("images/flow/start.png"), endEvent("images/flow/end.png"), groundEvent("images/flow/ground.png"), //
    userTask("images/flow/usertask.png"), scriptTask("images/flow/scriptask.png"), manualTask("images/flow/manualtask.png"), //
    sendTask("images/flow/sendtask.png"), serviceTask("images/flow/servicetask.png"), receiveTask("images/flow/receivetask.png"), //
    andGateway("images/flow/and.png"), orGateway("images/flow/or.png"), //
    parallelGateway("images/flow/parallel.png"), complexGateway("images/flow/complex.png"), exclusiveGateway("images/flow/exclusive.png"), inclusiveGateway("images/flow/inclusive.png"), //
    timeRobot("images/flow/timerobt.png"), thingTask("images/flow/thingtask.png"), flowEdge("images/blank.gif");

    private String image = "";

    FlowNodeType(String image) {
        this.image = image;
    }

    public String getImage() {
        return this.image;
    }
}
