package xw.flow.entity;

import xw.flow.constants.FlowNodeType;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "XFlowServiceTask", uniqueConstraints = {})
public class XFlowServiceTask extends XFlowNode {
    private static final long serialVersionUID = 1L;
    @Basic
    @Column(name = "implementedType")
    private String implementedType;
    @Basic
    @Column(name = "implementation")
    private String implementation = "";

    public static XFlowServiceTask newInstance(String nodeId, XFlowDefinition definition) {
        XFlowServiceTask flowtask = new XFlowServiceTask();
        flowtask.setNodeType(FlowNodeType.serviceTask);
        flowtask.setXFlowDefinition(definition);
        flowtask.setNodeId(nodeId);

        return flowtask;
    }

    public String getImplementedType() {
        return implementedType;
    }

    public void setImplementedType(String implementedType) {
        this.implementedType = implementedType;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }
}
