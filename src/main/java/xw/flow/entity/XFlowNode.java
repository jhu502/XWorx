package xw.flow.entity;

import com.flame.orm.XObject;
import xw.flow.constants.FlowNodeType;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class XFlowNode extends XObject {
	private static final long serialVersionUID = 1L;
	@Basic
    @Column(name = "nodeid", length = 100)
    private String nodeId = "";
    @Basic
    @Column(name = "name")
    private String name = "";
    @Basic
    @Column(name = "instructions")
    private String instructions = "";
    @Basic
    @Column(name = "axis_x")
    private long axisX = 0L;
    @Basic
    @Column(name = "axis_y")
    private long axisY = 0L;
    @ManyToOne(targetEntity = XFlowDefinition.class)
    @JoinColumn(name = "definitionId", foreignKey = @ForeignKey(name = "FLOWDEFINITION_ID_FK"))
    private XFlowDefinition definition;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "node_type")
    private FlowNodeType nodeType = FlowNodeType.thingTask;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
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

    public long getAxisX() {
        return axisX;
    }

    public void setAxisX(long axisX) {
        this.axisX = axisX;
    }

    public long getAxisY() {
        return axisY;
    }

    public void setAxisY(long axisY) {
        this.axisY = axisY;
    }

    public String getImage() {
        return this.getNodeType().getImage();
    }

    public XFlowDefinition getXFlowDefinition() {
        return definition;
    }

    public void setXFlowDefinition(XFlowDefinition definition) {
        this.definition = definition;
    }

    public FlowNodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(FlowNodeType cellType) {
        this.nodeType = cellType;
    }
}
