package xw.flow.entity;

import com.flame.orm.XObject;
import xw.flow.constants.FlowNodeType;

import jakarta.persistence.*;

@Entity
@Table(name = "XFlowEdge", uniqueConstraints = {})
public class XFlowEdge extends XObject {
	private static final long serialVersionUID = 1L;
	@Basic
    @Column(name = "edge_id", length = 100)
    private String edgeId = "";
    @Basic
    @Column(name = "source", length = 100)
    private String source = "";
    @Enumerated(EnumType.STRING)
    @Column(name = "srcType", nullable = false)
    private FlowNodeType srcType;
    @Basic
    @Column(name = "target", length = 100)
    private String target = "";
    @Enumerated(EnumType.STRING)
    @Column(name = "tgtType", nullable = false)
    private FlowNodeType tgtType;
    @Basic
    @Column(name = "routes")
    private String routes = "";
    @Basic
    @Column(name = "condition")
    private String condition = "";
    @ManyToOne(targetEntity = XFlowDefinition.class)
    @JoinColumn(name = "definitionId", foreignKey = @ForeignKey(name = "FLOWDEFINITION_ID_FK"))
    private XFlowDefinition definition;
    @Enumerated(EnumType.STRING)
    @Column(name = "nodeType", nullable = false)
    private FlowNodeType nodeType = FlowNodeType.flowEdge;

    public static XFlowEdge newInstance(String edgeId, XFlowDefinition definition) {
        XFlowEdge flowEdge = new XFlowEdge();
        flowEdge.setXFlowDefinition(definition);
        flowEdge.setEdgeId(edgeId);

        return flowEdge;
    }

    public String getSource() {
        return source;
    }

    public String getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(String edgeId) {
        this.edgeId = edgeId;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public FlowNodeType getSrcType() {
        return srcType;
    }

    public void setSrcType(FlowNodeType srcType) {
        this.srcType = srcType;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public FlowNodeType getTgtType() {
        return tgtType;
    }

    public void setTgtType(FlowNodeType tgtType) {
        this.tgtType = tgtType;
    }

    public String getRoutes() {
        return routes;
    }

    public void setRoutes(String routes) {
        this.routes = routes;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
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
