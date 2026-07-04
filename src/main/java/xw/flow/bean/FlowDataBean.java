package xw.flow.bean;

import java.util.ArrayList;
import java.util.List;

public class FlowDataBean {
    private String number;
    private String name;
    private String instructions;
    private List<FlowVariable> variables = new ArrayList<>();
    private List<FlowNodeVO> nodes = new ArrayList<>();
    private List<FlowEdgeVO> edges = new ArrayList<>();

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<FlowVariable> getVariables() {
        return variables;
    }

    public void setVariables(List<FlowVariable> variables) {
        this.variables = variables;
    }

    public void addVariables(FlowVariable variable) {
        this.variables.add(variable);
    }

    public List<FlowNodeVO> getNodes() {
        return nodes;
    }

    public void setNodes(List<FlowNodeVO> nodes) {
        this.nodes = nodes;
    }

    public List<FlowEdgeVO> getEdges() {
        return edges;
    }

    public void setEdges(List<FlowEdgeVO> edges) {
        this.edges = edges;
    }
}
