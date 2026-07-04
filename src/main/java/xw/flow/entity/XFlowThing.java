package xw.flow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "XFlowThing", uniqueConstraints = {})
public class XFlowThing extends XFlowNode {
    private static final long serialVersionUID = 1L;

    public static XFlowThing newInstance(String nodeId, XFlowDefinition definition) {
        XFlowThing flowThing = new XFlowThing();
        flowThing.setXFlowDefinition(definition);
        flowThing.setNodeId(nodeId);

        return flowThing;
    }
}
